package xyz.synse.datacenter.algorithms.motion;

import java.awt.image.BufferedImage;

public class MotionData {
    private boolean enabled = true;
    private BufferedImage lastImage;
    private BufferedImage detectedImage;

    private long processingTime;

    public BufferedImage getLastImage() {
        return lastImage;
    }

    public BufferedImage getDetectedImage() {
        return detectedImage;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }

    public void setLastImage(BufferedImage lastImage) {
        this.lastImage = lastImage;
    }

    public void setDetectedImage(BufferedImage detectedImage) {
        this.detectedImage = detectedImage;
    }

    public boolean canDetect(){
        return lastImage != null;
    }

    public void dispose(){
        this.detectedImage = null;
        this.lastImage = null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
