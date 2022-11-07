package xyz.synse.datacenter.hardware;

public class OperatingSystem extends Hardware {
    public static String getArch(){
        return osBean.getArch();
    }

    public static String getName(){
        return osBean.getName();
    }

    public static String getVersion(){
        return osBean.getVersion();
    }
}
