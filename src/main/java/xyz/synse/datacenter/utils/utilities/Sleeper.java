package xyz.synse.datacenter.utils.utilities;

public class Sleeper {
    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleepNano(int time){
        try {
            Thread.sleep(0L, time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
