package xyz.synse.datacenter.utils.frames;

import java.util.ArrayDeque;
import java.util.Queue;

public class FrameQueue {
    private final int framesLimit;
    private final Queue<byte[]> frameQueue = new ArrayDeque<>();

    public FrameQueue(int maxLimit){
        this.framesLimit = maxLimit;
    }

    public FrameQueue(){
        this.framesLimit = Integer.MAX_VALUE;
    }

    public void add(byte[] data) {
        if(frameQueue.size() > framesLimit)
            frameQueue.remove();
        frameQueue.add(data);
    }

    public byte[] take(){
        return frameQueue.remove();
    }

    public boolean hasNext(){
        return !frameQueue.isEmpty();
    }
}
