package com.inventory.service.impl;

import com.inventory.dto.SystemMetrics;
import com.inventory.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.util.Properties;

@Service
public class MonitorServiceImpl implements MonitorService {

    @Override
    public SystemMetrics getMetrics() {
        SystemMetrics metrics = new SystemMetrics();
        metrics.setCpu(getCpuMetrics());
        metrics.setMemory(getMemoryMetrics());
        metrics.setDisk(getDiskMetrics());
        metrics.setJvm(getJvmMetrics());
        metrics.setApp(getAppMetrics());
        return metrics;
    }

    private SystemMetrics.CpuMetrics getCpuMetrics() {
        SystemMetrics.CpuMetrics cpu = new SystemMetrics.CpuMetrics();
        cpu.setCores(Runtime.getRuntime().availableProcessors());
        try {
            com.sun.management.OperatingSystemMXBean osBean = 
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double systemLoad = osBean.getSystemCpuLoad();
            cpu.setUsagePercent(systemLoad * 100);
        } catch (Exception e) {
            cpu.setUsagePercent(0);
        }
        return cpu;
    }

    private SystemMetrics.MemoryMetrics getMemoryMetrics() {
        SystemMetrics.MemoryMetrics mem = new SystemMetrics.MemoryMetrics();
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory();
        long free = rt.freeMemory();
        long used = total - free;
        mem.setTotal(total);
        mem.setFree(free);
        mem.setUsed(used);
        mem.setUsagePercent((used * 100.0) / total);
        return mem;
    }

    private SystemMetrics.DiskMetrics getDiskMetrics() {
        SystemMetrics.DiskMetrics disk = new SystemMetrics.DiskMetrics();
        try {
            java.io.File diskPath = new java.io.File("/");
            long total = diskPath.getTotalSpace();
            long free = diskPath.getFreeSpace();
            long used = total - free;
            disk.setTotal(total);
            disk.setFree(free);
            disk.setUsed(used);
            disk.setUsagePercent((used * 100.0) / total);
        } catch (Exception e) {
            disk.setTotal(0);
            disk.setUsed(0);
            disk.setFree(0);
            disk.setUsagePercent(0);
        }
        return disk;
    }

    private SystemMetrics.JvmMetrics getJvmMetrics() {
        SystemMetrics.JvmMetrics jvm = new SystemMetrics.JvmMetrics();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        long heapTotal = memoryBean.getHeapMemoryUsage().getCommitted();
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapFree = heapTotal - heapUsed;

        jvm.setHeapTotal(heapTotal);
        jvm.setHeapUsed(heapUsed);
        jvm.setHeapFree(heapFree);
        jvm.setHeapUsagePercent((heapUsed * 100.0) / heapTotal);
        jvm.setThreadCount(threadBean.getThreadCount());

        return jvm;
    }

    private SystemMetrics.AppMetrics getAppMetrics() {
        SystemMetrics.AppMetrics app = new SystemMetrics.AppMetrics();
        try {
            Properties props = System.getProperties();
            long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
            app.setUptime(uptime);
            app.setActiveSession(0);
            app.setQps(0);
            app.setAvgResponseTime(0);
            app.setErrorRate(0);
        } catch (Exception e) {
            app.setUptime(0);
            app.setActiveSession(0);
            app.setQps(0);
            app.setAvgResponseTime(0);
            app.setErrorRate(0);
        }
        return app;
    }
}
