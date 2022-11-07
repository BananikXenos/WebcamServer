package xyz.synse.datacenter.utils.image;

import xyz.synse.datacenter.cameras.Camera;
import xyz.synse.datacenter.utils.frames.Framerate;
import xyz.synse.datacenter.utils.frames.FramerateCounter;
import xyz.synse.datacenter.utils.utilities.Pair;
import xyz.synse.datacenter.utils.utilities.Sleeper;
import xyz.synse.datacenter.utils.utilities.Stopwatch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImageWindow {
    private final Camera parent;
    private final MouseAdapter mouseAdapter;
    private final AtomicBoolean shouldUpdate = new AtomicBoolean(true);
    private final Framerate framerate;
    private final FramerateCounter framerateCounter = new FramerateCounter();
    private final Thread updateThread;
    private JFrame frame;
    private JLabel label;
    private final Stopwatch hertzStopwatch = new Stopwatch();

    @Deprecated(since = "17.10.2022")
    public ImageWindow(Camera parent, MouseAdapter mouseAdapter, int fps) {
        this.framerate = new Framerate(fps);
        this.parent = parent;
        this.mouseAdapter = mouseAdapter;
        this.updateThread = new Thread(() -> {
            while (shouldUpdate.get()){
                if(framerate.canContinue()) {
                    update();
                    framerate.onFrame();
                    framerateCounter.onFrame();
                }
            }
        });
        this.updateThread.start();
    }

    public ImageWindow(Camera parent, MouseAdapter mouseAdapter) {
        this.framerate = new Framerate(getHertz());
        this.parent = parent;
        this.mouseAdapter = mouseAdapter;
        this.updateThread = new Thread(() -> {
            hertzStopwatch.start();
            while (shouldUpdate.get()){
                if(framerate.canContinue()) {
                    update();
                    framerate.onFrame();
                    framerateCounter.onFrame();
                    hertzStopwatch.end();

                    if(hertzStopwatch.getTook() > 1000L) {
                        framerate.setFramerate(getHertz());
                        hertzStopwatch.start();
                    }
                }
            }
        });
        this.updateThread.start();
    }

    public int getHertz() {
        if(frame == null)
            return 30;

        Window myWindow = frame;
        GraphicsConfiguration config = myWindow.getGraphicsConfiguration();
        GraphicsDevice myScreen = config.getDevice();

        return myScreen.getDisplayMode().getRefreshRate();
    }

    public FramerateCounter getFramerateCounter() {
        return framerateCounter;
    }

    public Framerate getFramerate() {
        return framerate;
    }

    private void update() {
        BufferedImage image = null;

        // Display image on window
        switch (parent.getCurrentView()) {
            case MAIN_VIEW -> {
                // Default main processed
                Pair<BufferedImage, Stopwatch> pair = parent.renderImage();
                if(pair.getA() == null)
                    return;

                image = pair.getA();
            }
            case RAW_IMAGE -> {
                // Raw image of camera
                image = parent.getRawImage();
            }
            case MOTION_PROCESSED -> {
                // Grayed and blurred image used for motion detection
                image = parent.getMotionData().getLastImage();
            }
        }

        if(image == null)
            return;

        if (frame == null) {
            frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            label = new JLabel();
            label.addMouseListener(mouseAdapter);
            frame.getContentPane().add(label, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.setSize(image.getWidth(), image.getHeight());
            frame.setVisible(true);
        }

        label.setIcon(new ImageIcon(image));
        frame.setTitle("ID: " + parent.getCameraID() + " Hz: " + framerate.getFramerate());
        frame.pack();
    }

    public Thread getUpdateThread() {
        return updateThread;
    }

    public void close() {
        if (this.frame == null)
            return;

        this.shouldUpdate.set(false);
        this.frame.setVisible(false); //you can't see me!
        this.frame.dispose(); //Destroy the JFrame object
    }
}
