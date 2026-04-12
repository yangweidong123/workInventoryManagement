package com.inventory.dto;

import lombok.Data;

@Data
public class SystemMetrics {
    private CpuMetrics cpu;
    private MemoryMetrics memory;
    private DiskMetrics disk;
    private JvmMetrics jvm;
    private AppMetrics app;

    @Data
    public static class CpuMetrics {
        private double usagePercent;
        private int cores;
    }

    @Data
    public static class MemoryMetrics {
        private long total;
        private long used;
        private long free;
        private double usagePercent;
    }

    @Data
    public static class DiskMetrics {
        private long total;
        private long used;
        private long free;
        private double usagePercent;
    }

    @Data
    public static class JvmMetrics {
        private long heapTotal;
        private long heapUsed;
        private long heapFree;
        private double heapUsagePercent;
        private long nonHeapTotal;
        private long nonHeapUsed;
        private int threadCount;
        private long gcCount;
    }

    @Data
    public static class AppMetrics {
        private long uptime;
        private int activeSession;
        private double qps;
        private double avgResponseTime;
        private double errorRate;
    }
}
