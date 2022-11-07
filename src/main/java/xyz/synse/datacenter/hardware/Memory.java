package xyz.synse.datacenter.hardware;

public class Memory extends Hardware {
    public static long getFree(){
        return osBean.getFreeMemorySize();
    }

    public static long getTotal(){
        return osBean.getTotalMemorySize();
    }
}
