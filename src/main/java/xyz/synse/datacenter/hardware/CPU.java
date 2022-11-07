package xyz.synse.datacenter.hardware;

import org.jetbrains.annotations.Range;

public class CPU extends Hardware {

    @Range(from = 0, to = 1)
    public static double getOverallUsage() {
        return osBean.getCpuLoad();
    }

    @Range(from = 0, to = 1)
    public static double getProcessUsage() {
        return osBean.getProcessCpuLoad();
    }

    public static double getAvailableProcessors() {
        return osBean.getAvailableProcessors();
    }
}
