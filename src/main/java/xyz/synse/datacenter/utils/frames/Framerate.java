package xyz.synse.datacenter.utils.frames;

public class Framerate {
    private int framerate;
    private long lastFrame = System.currentTimeMillis();

    public Framerate(int framerate) {
        this.framerate = framerate;
    }

    public boolean canContinue(){
        return ((System.currentTimeMillis() - this.lastFrame) >= (1000D / this.framerate));
    }

    public void onFrame(){
        this.lastFrame = System.currentTimeMillis();
    }

    public int getFramerate() {
        return framerate;
    }

    public void setFramerate(int framerate) {
        this.framerate = framerate;
    }
}
