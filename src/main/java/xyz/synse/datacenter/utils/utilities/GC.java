package xyz.synse.datacenter.utils.utilities;

import xyz.synse.datacenter.logger.Logger;
import xyz.synse.datacenter.utils.numbers.NumberUtils;

public class GC {
    public static void runGC() {
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        long memoryMax = runtime.maxMemory();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
        double memoryUsedPercent = (memoryUsed * 100.0) / memoryMax;
        Logger.i("Memory usage: " + NumberUtils.round(memoryUsedPercent, 3) + "%");
    }
}
