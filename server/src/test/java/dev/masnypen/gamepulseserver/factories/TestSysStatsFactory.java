package dev.masnypen.gamepulseserver.factories;

import dev.masnypen.gamepulseserver.models.sysstats.*;

public class TestSysStatsFactory {

    public static SysStats create() {
        SysStats stats = new SysStats();

        // CPU
        var cpu = new CPUData();
        cpu.setName("Ryzen");
        cpu.setThreads(8);
        cpu.setTemperature(60);
        cpu.setFrequency(3600);
        cpu.setUsedPercentage(40);
        stats.setCpu(cpu);

        // MEMORY
        var mem = new MemoryData();
        mem.setTotal(16000);
        mem.setUsed(8000);
        mem.setTotalSwap(2000);
        mem.setUsedSwap(500);
        stats.setMemory(mem);

        // GPU
        var gpu = new GPUData();
        gpu.setVendor("NVIDIA");
        gpu.setModel("RTX");
        gpu.setTotalVram(8000);
        gpu.setUsedVram(3000);
        gpu.setLoadPercentage(50);
        gpu.setTemperature(70);
        stats.setGpu(gpu);

        // SYSTEM
        var system = new SystemData();
        system.setName("Linux");
        system.setKernel("6.x");
        system.setOsVersion("Ubuntu");
        system.setHostname("host");
        stats.setSystem(system);

        // DISK
        var disk = new DiskData();
        disk.setDevice("/dev/sda");
        disk.setFilesystem("ext4");
        disk.setTotalSpace(100000);
        disk.setUsedSpace(50000);
        disk.setMountedOn("/");

        stats.setDisks(java.util.List.of(disk));

        return stats;
    }
}