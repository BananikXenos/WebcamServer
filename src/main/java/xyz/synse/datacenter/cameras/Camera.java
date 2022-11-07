package xyz.synse.datacenter.cameras;

import org.jetbrains.annotations.Nullable;
import xyz.synse.datacenter.MantleAPI;
import xyz.synse.datacenter.algorithms.motion.MotionData;
import xyz.synse.datacenter.algorithms.motion.MotionDetection;
import xyz.synse.datacenter.service.services.CamerasService;
import xyz.synse.datacenter.utils.frames.FrameCache;
import xyz.synse.datacenter.utils.frames.FramerateCounter;
import xyz.synse.datacenter.utils.frames.View;
import xyz.synse.datacenter.utils.graphics.ColorBlend;
import xyz.synse.datacenter.utils.graphics.FontUtils;
import xyz.synse.datacenter.utils.graphics.GraphicsUtils;
import xyz.synse.datacenter.utils.graphics.IconUtils;
import xyz.synse.datacenter.utils.image.ImageData;
import xyz.synse.datacenter.utils.image.ImageUtils;
import xyz.synse.datacenter.utils.image.ImageWindow;
import xyz.synse.datacenter.utils.image.NV21;
import xyz.synse.datacenter.utils.numbers.ArrayUtils;
import xyz.synse.datacenter.utils.numbers.DeviceOrientation;
import xyz.synse.datacenter.utils.numbers.NumberUtils;
import xyz.synse.datacenter.utils.utilities.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Camera {
    // ID of camera
    private final String cameraID;
    // Socket
    private Socket connection;
    // Raw image converted to RGB
    private BufferedImage rawImage;
    // Last time a frame was sent
    private final Stopwatch lastFrameSent = new Stopwatch();
    // Cache of frames from last 5 seconds
    private final FrameCache frameCache = new FrameCache(5 * 30);
    // FPS Counter
    private final FramerateCounter framerateCounter = new FramerateCounter();
    // Fps list
    private final ArrayList<Integer> fpsList = new ArrayList<>();
    // Average fps
    private double averageFPS = 60;
    // View used to display images
    private View currentView = View.MAIN_VIEW;
    // If to draw more information
    private final AtomicBoolean advancedInfo = new AtomicBoolean(false);
    // Data used for image detection
    private final MotionData motionData = new MotionData();
    // Mouse listener to change views
    private final MouseAdapter windowMouseAdapter = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                currentView = View.next(currentView);
            } else if (e.getButton() == MouseEvent.BUTTON2) {
                motionData.setEnabled(!motionData.isEnabled());
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                advancedInfo.set(!advancedInfo.get());
            }
        }
    };
    // Window for image
    private final ImageWindow imageWindow = new ImageWindow(this, windowMouseAdapter);
    // Motion detection algorithm
    private final MotionDetection motionDetector = new MotionDetection();
    // Orientation
    private final Orientation orientation = new Orientation();
    // Angle utilities
    private final DeviceOrientation deviceOrientation = new DeviceOrientation();
    // Angle of camera
    private int cameraAngle;
    // Last camera angle. Used to fix motion detection error after rotating camera
    private float lastCameraAngle = 0;
    // Battery level
    private BatteryInfo batteryInfo = new BatteryInfo(0, false, "NONE");

    /**
     * Main constructor of camera
     *
     * @param cameraID id of camera
     */
    public Camera(String cameraID) {
        this.cameraID = cameraID;
    }

    /**
     * Called when frame is received.
     * Processes YUVImageData received and calculations for motion data.
     *
     * @param connection          connection
     * @param imageData           image Data
     * @param orientation         orientation of the phone
     * @param cameraAngleHardware built-in camera angle
     * @param batteryInfo         battery information
     */
    public void onFrame(Socket connection, ImageData imageData, Orientation orientation, float cameraAngleHardware, BatteryInfo batteryInfo) {
        this.connection = connection;
        // Set battery level
        this.batteryInfo = batteryInfo;
        // Set orientation
        this.orientation.set(orientation);
        // Compute camera angle from angle and built-in camera angle
        this.deviceOrientation.calculate(this.orientation.getRoll(), this.orientation.getPitch());
        this.cameraAngle = deviceOrientation.getOrientation() + (int) cameraAngleHardware;

        // Check if we rotated
        if (this.lastCameraAngle != cameraAngle) {
            // Set null when rotated
            this.motionData.setLastImage(null);
        }
        // Add to cache
        this.frameCache.add(imageData, cameraAngle);
        // FPS counter
        this.framerateCounter.onFrame();
        ArrayUtils.addAndUpdate(fpsList, this.framerateCounter.getFps(), 100);
        this.averageFPS = NumberUtils.calculateAverageInt(fpsList);
        // Last time sent
        this.lastFrameSent.start();

        // Convert byte array in YUV format to RGB Buffered Image (10ms)
        rawImage = ImageUtils.rotateImage(NV21.YUV_NV21_TO_RGB(imageData), cameraAngle);

        // Stopwatch to measure time it took to process motion
        Stopwatch motionStopwatch = new Stopwatch();
        motionStopwatch.start();

        // If motion detection is enabled
        if (motionData.isEnabled()) {
            // Copy image to be used to draw detected motion
            BufferedImage detectedImage = ImageUtils.deepCopy(rawImage);
            // Grayed and Blurred image to be used in motion detection
            BufferedImage motionFiltered = motionDetector.filter(rawImage);

            // If there is any previous image. Only used on first frame where there isn't any previous one.
            if (motionData.canDetect()) {
                // Detect motion and check if there was any (40ms)
                boolean motion = detectMotion(motionFiltered);

                // Check if there was any motion
                if (motion)
                    // Draw any motion
                    drawMotion(detectedImage);
            }

            motionData.setDetectedImage(detectedImage);
            // Grayed and Blurred image to be used in motion detection
            motionData.setLastImage(motionFiltered);
        } else {
            // Copy image to be used to draw detected motion
            motionData.setDetectedImage(ImageUtils.deepCopy(rawImage));
        }

        // Stop motionStopwatch
        motionStopwatch.end();
        // Set the processing time
        this.motionData.setProcessingTime(motionStopwatch.getTook());
        // Set last angle
        this.lastCameraAngle = cameraAngle;
    }

    /**
     * Draws camera information on image
     *
     * @param img image
     */
    public Stopwatch drawCamInfo(BufferedImage img) {
        // Stopwatch to get rendering time
        final Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();

        // Create graphics
        GraphicsUtils graphics = new GraphicsUtils(img);
        // Enable AntiAliasing
        graphics.enableAntiAliasing();

        // Set color and draw information
        // More advanced info
        if (advancedInfo.get()) {
            graphics.color(new Color(31, 31, 31));
            graphics.roundBox(-10, -10, 20 + graphics.fontMetrics().stringWidth("ID: " + cameraID), (int) (30 + (graphics.font().getSize() * 10.5f)), 20);
        }
        graphics.color(new Color(50, 50, 50));
        graphics.box(0, 0, 10 + graphics.fontMetrics().stringWidth("ID: " + cameraID), 4 + graphics.font().getSize());
        graphics.color(new Color(227, 226, 226));
        graphics.font(FontUtils.robotoBold12);
        graphics.text("ID: " + cameraID, 5, 12);
        graphics.color(new Color(165, 165, 165));

        // More advanced info
        if (advancedInfo.get()) {
            graphics.font(FontUtils.roboto12);
            graphics.color(new Color(205, 205, 205));
            graphics.text("FPS: ", 5, 16 + graphics.font().getSize());
            graphics.color(new Color(165, 165, 165));
            graphics.text("     - Camera: " + framerateCounter.getFps(), 5, 16 + (graphics.font().getSize() * 2f));
            graphics.text("     - Window: " + imageWindow.getFramerateCounter().getFps() + "/" + imageWindow.getFramerate().getFramerate(), 5, 16 + (graphics.font().getSize() * 3f));
            graphics.color(new Color(205, 205, 205));
            graphics.text("Data: " + (this.frameCache.peek() != null && this.frameCache.peek().getImageData() != null ? NumberUtils.round(this.frameCache.peek().getImageData().getData().length / 1_000_000D, 3) + "MB" : "NaN"), 5, 18 + (graphics.font().getSize() * 4f));
            graphics.text("Delay: " + getDelay() + "ms", 5, 20 + (graphics.font().getSize() * 5f));
            graphics.text("CamAngle: " + cameraAngle, 5, 20 + (graphics.font().getSize() * 6f));
            graphics.text("Orientation: ", 5, 20 + (graphics.font().getSize() * 7f));
            graphics.color(new Color(165, 165, 165));
            graphics.text("     - Azimuth: " + NumberUtils.round(orientation.getAzimuth(), 2), 5, 20 + (graphics.font().getSize() * 8f));
            graphics.text("     - Pitch: " + NumberUtils.round(orientation.getPitch(), 2), 5, 20 + (graphics.font().getSize() * 9f));
            graphics.text("     - Roll: " + NumberUtils.round(orientation.getRoll(), 2), 5, 20 + (graphics.font().getSize() * 10f));
        }

        // More advanced info
        // Set color & draw motion detection processing time
        graphics.font(FontUtils.robotoBold12);
        if (motionData.isEnabled() && advancedInfo.get()) {
            graphics.color(new Color(19, 19, 19));
            graphics.roundBox(img.getWidth() - graphics.fontMetrics().stringWidth("Motion Detection:" + motionData.getProcessingTime() + "ms") - 16, img.getHeight() - graphics.fontMetrics().getHeight() - 10, img.getWidth() - (img.getWidth() - graphics.fontMetrics().stringWidth("Motion Detection:" + motionData.getProcessingTime() + "ms") - 16), graphics.fontMetrics().getHeight() + 14, 20);
            graphics.color(ColorBlend.blend(new Color(79, 209, 17), new Color(192, 2, 2), motionData.getProcessingTime() / 65F));
            graphics.circle(img.getWidth() - graphics.fontMetrics().stringWidth("Motion Detection:" + motionData.getProcessingTime() + "ms") - 10, img.getHeight() - graphics.fontMetrics().getHeight() + 4, 3);
            graphics.font(FontUtils.robotoBold12);
            graphics.color(new Color(190, 190, 190));
            graphics.text("Motion Detection: " + motionData.getProcessingTime() + "ms", img.getWidth() - graphics.fontMetrics().stringWidth("Motion Detection:" + motionData.getProcessingTime() + "ms") - 4, img.getHeight() - graphics.fontMetrics().getHeight() + 8);
        } else if (motionData.isEnabled() && !advancedInfo.get()) {
            graphics.color(new Color(19, 19, 19));
            graphics.roundBox(img.getWidth() - graphics.fontMetrics().stringWidth("Motion Detection") - 16, img.getHeight() - graphics.fontMetrics().getHeight() - 10, img.getWidth() - (img.getWidth() - graphics.fontMetrics().stringWidth("Motion Detection") - 16), graphics.fontMetrics().getHeight() + 14, 20);
            graphics.color(new Color(79, 209, 17));
            graphics.circle(img.getWidth() - graphics.fontMetrics().stringWidth("Motion Detection") - 10, img.getHeight() - graphics.fontMetrics().getHeight() + 4, 3);
            graphics.font(FontUtils.robotoBold12);
            graphics.color(new Color(190, 190, 190));
            graphics.text("Motion Detection", img.getWidth() - graphics.fontMetrics().stringWidth("Motion Detection") - 4, img.getHeight() - graphics.fontMetrics().getHeight() + 8);
        } else {
            graphics.color(new Color(19, 19, 19));
            graphics.roundBox(img.getWidth() - graphics.fontMetrics().stringWidth("Motion Detection") - 16, img.getHeight() - graphics.fontMetrics().getHeight() - 10, img.getWidth() - (img.getWidth() - graphics.fontMetrics().stringWidth("Motion Detection") - 16), graphics.fontMetrics().getHeight() + 14, 20);
            graphics.color(new Color(192, 2, 2));
            graphics.circle(img.getWidth() - graphics.fontMetrics().stringWidth("Motion Detection") - 10, img.getHeight() - graphics.fontMetrics().getHeight() + 4, 3);
            graphics.font(FontUtils.robotoBold12);
            graphics.color(new Color(190, 190, 190));
            graphics.text("Motion Detection", img.getWidth() - graphics.fontMetrics().stringWidth("Motion Detection") - 4, img.getHeight() - graphics.fontMetrics().getHeight() + 8);
        }

        // Draw battery
        // Background
        graphics.color(new Color(50, 50, 50));
        graphics.roundBox(2, img.getHeight() - 10, 100, 8, 5);

        if (!batteryInfo.isCharging()) {
            // Battery bar
            Color batteryColor = ColorBlend.blend(new Color(121, 18, 18), new Color(63, 157, 16), batteryInfo.getBatteryLevel() / 100F);
            graphics.color(batteryColor);
            graphics.roundBox(2, img.getHeight() - 10, batteryInfo.getBatteryLevel(), 8, 5);
            // Text percentage
            graphics.font(FontUtils.robotoBold8);
            graphics.color(new Color(245, 245, 245));
            graphics.text(batteryInfo.getBatteryLevel() + "%", 50 - (graphics.fontMetrics().stringWidth(batteryInfo.getBatteryLevel() + "%") / 2f), img.getHeight() - 3);
        } else {
            // Battery bar
            Color batteryColor = new Color(23, 165, 222);
            graphics.color(batteryColor);
            graphics.roundBox(2, img.getHeight() - 10, batteryInfo.getBatteryLevel(), 8, 5);
            // Text percentage
            graphics.font(FontUtils.robotoBold8);
            graphics.color(new Color(245, 245, 245));
            graphics.text(batteryInfo.getChargePlug(), 50 - (graphics.fontMetrics().stringWidth(batteryInfo.getChargePlug()) / 2f), img.getHeight() - 3);
        }

        // Check if camera is connected
        if (connection == null) {
            graphics.font(FontUtils.robotoBold18);
            graphics.image(IconUtils.warningLogo, (int) ((img.getWidth() / 2D) - 66.5D), (int) ((img.getHeight() - graphics.fontMetrics().getHeight()) / 2F - 75), 133, 100);
            graphics.color(new Color(255, 30, 71));
            graphics.text("CAMERA DISCONNECTED", (img.getWidth() - graphics.fontMetrics().stringWidth("CAMERA DISCONNECTED")) / 2F, (img.getHeight() - graphics.fontMetrics().getHeight()) / 2F + 25);
            graphics.font(FontUtils.robotoBold12);

            CamerasService camerasService = MantleAPI.INSTANCE.servicesManager.getService(CamerasService.class);
            if (camerasService != null) {
                long timeLeftMs = (camerasService.getExpireTime() - lastFrameSent.getTook());
                double timeout = NumberUtils.round(timeLeftMs / 1000D, 1);
                graphics.text("Waiting " + timeout + "s", (img.getWidth() - graphics.fontMetrics().stringWidth("Waiting " + timeout + "s")) / 2F, (img.getHeight() + graphics.fontMetrics().getHeight()) / 2F + 25);
            }
        } else {
            long delay = getDelay();
            if (delay > 1000) {
                graphics.font(FontUtils.robotoBold18);
                graphics.image(IconUtils.warningLogo, (int) ((img.getWidth() / 2D) - 66.5D), (int) ((img.getHeight() - graphics.fontMetrics().getHeight()) / 2F - 75), 133, 100);
                graphics.color(new Color(255, 30, 71));
                graphics.text("Last frame was longer than", (img.getWidth() - graphics.fontMetrics().stringWidth("Last frame was longer than")) / 2F, (img.getHeight() - graphics.fontMetrics().getHeight()) / 2F + 25);
                graphics.font(FontUtils.robotoBold12);

                graphics.text(delay + "ms ago", (img.getWidth() - graphics.fontMetrics().stringWidth(delay + "ms ago")) / 2F, (img.getHeight() + graphics.fontMetrics().getHeight()) / 2F + 25);
            } else {
                // Instability indicator
                double oneThird = averageFPS / 2D;

                // Check if is bellow 20%
                if (framerateCounter.getFps() < oneThird) {
                    graphics.font(FontUtils.robotoBold18);
                    graphics.image(IconUtils.warningLogo, (int) ((img.getWidth() / 2D) - 66.5D), (int) ((img.getHeight() - graphics.fontMetrics().getHeight()) / 2F - 75), 133, 100);
                    graphics.color(new Color(255, 30, 71));
                    graphics.text("Unstable FPS. 33% of average", (img.getWidth() - graphics.fontMetrics().stringWidth("Unstable FPS. 33% of average")) / 2F, (img.getHeight() - graphics.fontMetrics().getHeight()) / 2F + 25);
                }
            }
        }

        // Clear & release system resources
        graphics.end();

        stopwatch.end();
        return stopwatch;
    }

    /**
     * Detects motion
     *
     * @param image gray & blurred processed image
     * @return motion detected
     */
    public boolean detectMotion(BufferedImage image) {
        return motionDetector.detect(motionData.getLastImage(), image);
    }

    /**
     * Draws detected motion on image
     *
     * @param img image
     */
    private void drawMotion(BufferedImage img) {
        // Create graphics
        GraphicsUtils graphics = new GraphicsUtils(img);
        // Enable AntiAliasing
        graphics.enableAntiAliasing();

        //graphics.color(new Color(245,245,245, 80));
        //graphics.complexLines(motionDetector.getPoints());

        // Draw dots
        graphics.color(new Color(255, 54, 171));
        graphics.circles(motionDetector.getPoints(), 4);

        // Set color and draw cog
        graphics.color(new Color(100, 44, 169));
        graphics.circle(motionDetector.getCog().x, motionDetector.getCog().y, 6);

        // Clear & release system resources
        graphics.end();
    }

    @Nullable
    public Pair<BufferedImage, Stopwatch> renderImage() {
        // Copy image
        BufferedImage image = ImageUtils.deepCopy(motionData.getDetectedImage());
        // Draw info
        if (image != null) {
            Stopwatch stopwatch = drawCamInfo(image);
            return new Pair<>(image, stopwatch);
        }

        return new Pair<>(null, null);
    }

    public MotionData getMotionData() {
        return motionData;
    }

    public String getCameraID() {
        return cameraID;
    }

    public FrameCache getFrameCache() {
        return frameCache;
    }

    public BufferedImage getRawImage() {
        return rawImage;
    }

    public long getLastFrameSent() {
        return lastFrameSent.getStartTime();
    }

    public long getDelay() {
        lastFrameSent.end();
        return lastFrameSent.getTook();
    }

    public FramerateCounter getFrameRateCounter() {
        return framerateCounter;
    }

    public ImageWindow getImageWindow() {
        return imageWindow;
    }

    public void setCurrentView(View currentView) {
        this.currentView = currentView;
    }

    public View getCurrentView() {
        return currentView;
    }

    public BatteryInfo getBatteryInfo() {
        return batteryInfo;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public float getCameraAngle() {
        return cameraAngle;
    }

    public Socket getConnection() {
        return connection;
    }

    public void dispose() {
        imageWindow.close();
        rawImage = null;
        frameCache.clear();
        motionData.dispose();
    }

    public void socketDisconnected() {
        this.connection = null;
    }
}
