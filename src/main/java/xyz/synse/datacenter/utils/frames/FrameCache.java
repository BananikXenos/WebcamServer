package xyz.synse.datacenter.utils.frames;

import xyz.synse.datacenter.utils.image.ImageData;

import java.util.ArrayDeque;
import java.util.Queue;

public class FrameCache {
    private final int framesLimit;
    private final Queue<CachedFrame> frameQueue = new ArrayDeque<>();

    public FrameCache(int maxLimit){
        this.framesLimit = maxLimit;
    }

    public FrameCache(){
        this.framesLimit = Integer.MAX_VALUE;
    }

    public void add(ImageData imageData, int cameraAngle) {
        if(frameQueue.size() > framesLimit)
            frameQueue.remove();
        frameQueue.add(new CachedFrame(imageData, cameraAngle));
    }

    public CachedFrame take(){
        return frameQueue.remove();
    }

    public boolean hasNext(){
        return !frameQueue.isEmpty();
    }

    public CachedFrame peek(){
        return frameQueue.peek();
    }

    public void clear() {
        this.frameQueue.clear();
    }

    public static class CachedFrame{
        private final ImageData imageData;
        private final int cameraAngle;

        public CachedFrame(ImageData imageData, int cameraAngle) {
            this.imageData = imageData;
            this.cameraAngle = cameraAngle;
        }

        public int getCameraAngle() {
            return cameraAngle;
        }

        public ImageData getImageData() {
            return imageData;
        }
    }
}
