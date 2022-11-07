package xyz.synse.datacenter.hardware;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public abstract class Hardware {
    static final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
            OperatingSystemMXBean.class);
}
