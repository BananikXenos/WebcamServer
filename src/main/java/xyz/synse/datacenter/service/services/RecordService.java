package xyz.synse.datacenter.service.services;

import xyz.synse.datacenter.cameras.Camera;
import xyz.synse.datacenter.logger.Logger;
import xyz.synse.datacenter.service.Service;
import xyz.synse.datacenter.utils.frames.Framerate;
import xyz.synse.datacenter.utils.video.VideoCompiler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RecordService extends Service {
    private final File file;
    private final Camera camera;
    private final long requestedFrames;
    private long frames;
    private final Framerate framerate;
    private VideoCompiler videoCompiler;

    public RecordService(Camera camera, File file, long seconds, int fps) {
        this.file = file;
        this.camera = camera;
        this.framerate = new Framerate(fps);
        this.requestedFrames = seconds * fps;
    }

    @Override
    public void onStart() {
        try {
            videoCompiler = new VideoCompiler(file, this.framerate.getFramerate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doWork() {
        if (frames < requestedFrames) {
            BufferedImage image = camera.getRawImage();
            if (framerate.canContinue() && image != null) {
                try {
                    videoCompiler.appendImage(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                framerate.onFrame();
                frames++;
                Logger.i("Recording camera " + camera.getCameraID() + ": " + (((double)frames / (double)requestedFrames) * 100D) + "%");
            }
        } else exit();
    }

    @Override
    public void onStop() {
        try {
            videoCompiler.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
