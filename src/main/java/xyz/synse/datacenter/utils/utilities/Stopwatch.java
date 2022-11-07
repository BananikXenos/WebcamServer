package xyz.synse.datacenter.utils.utilities;

public class Stopwatch {
    private long startTime = System.currentTimeMillis();
    private long took = 0;

    public void start(){
        this.startTime = System.currentTimeMillis();
    }

    public long getStartTime() {
        return startTime;
    }

    public void end(){
        this.took = (System.currentTimeMillis() - this.startTime);
    }

    public long getTook() {
        return took;
    }
}
