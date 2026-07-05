package dev.masnypen.gamepulseserver.services.dashboard;

import dev.masnypen.gamepulseserver.models.dashboard.DashboardSnapshotPayload;
import dev.masnypen.gamepulseserver.models.dashboard.MetricSection;
import dev.masnypen.gamepulseserver.models.metrics.*;
import dev.masnypen.gamepulseserver.models.sysstats.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class DashboardSnapshotMapper {

    public DashboardSnapshotPayload fromSysStats(
            UUID agentId,
            Long snapshotId,
            Instant capturedAt,
            SysStats stats,
            Set<MetricSection> metricSections
    ) {
        DashboardSnapshotPayload payload = basePayload(agentId, snapshotId, capturedAt);

        if (metricSections.contains(MetricSection.SYSTEM)) {
            payload.setSystem(stats.getSystem());
        }
        if (metricSections.contains(MetricSection.CPU)) {
            payload.setCpu(stats.getCpu());
        }
        if (metricSections.contains(MetricSection.MEMORY)) {
            payload.setMemory(stats.getMemory());
        }
        if (metricSections.contains(MetricSection.GPU)) {
            payload.setGpu(stats.getGpu());
        }
        if (metricSections.contains(MetricSection.DISKS)) {
            payload.setDisks(stats.getDisks());
        }

        return payload;
    }

    public DashboardSnapshotPayload fromEntities(
            SystemSnapshotEntity snapshot,
            SystemDataEntity systemData,
            CpuMetricsEntity cpuMetrics,
            MemoryMetricsEntity memoryMetrics,
            GpuMetricsEntity gpuMetrics,
            List<DiskMetricsEntity> diskMetrics,
            Set<MetricSection> metricSections
    ) {
        DashboardSnapshotPayload payload = basePayload(snapshot.getAgent().getId(), snapshot.getId(), snapshot.getCapturedAt());

        if (metricSections.contains(MetricSection.SYSTEM) && systemData != null) {
            SystemData system = new SystemData();
            system.setName(systemData.getName());
            system.setKernel(systemData.getKernel());
            system.setOsVersion(systemData.getOsVersion());
            system.setHostname(systemData.getHostname());
            payload.setSystem(system);
        }

        if (metricSections.contains(MetricSection.CPU) && cpuMetrics != null) {
            CPUData cpu = new CPUData();
            cpu.setName(cpuMetrics.getName());
            cpu.setThreads(cpuMetrics.getThreads());
            cpu.setTemperature(cpuMetrics.getTemperature());
            cpu.setFrequency(cpuMetrics.getFrequency());
            cpu.setUsedPercentage(cpuMetrics.getUsedPercentage());
            payload.setCpu(cpu);
        }

        if (metricSections.contains(MetricSection.MEMORY) && memoryMetrics != null) {
            MemoryData memory = new MemoryData();
            memory.setTotal(memoryMetrics.getTotal());
            memory.setUsed(memoryMetrics.getUsed());
            memory.setTotalSwap(memoryMetrics.getTotalSwap());
            memory.setUsedSwap(memoryMetrics.getUsedSwap());
            payload.setMemory(memory);
        }

        if (metricSections.contains(MetricSection.GPU) && gpuMetrics != null) {
            GPUData gpu = new GPUData();
            gpu.setVendor(gpuMetrics.getVendor());
            gpu.setModel(gpuMetrics.getModel());
            gpu.setTotalVram(gpuMetrics.getTotalVram());
            gpu.setUsedVram(gpuMetrics.getUsedVram());
            gpu.setLoadPercentage(gpuMetrics.getLoadPercentage());
            gpu.setTemperature(gpuMetrics.getTemperature());
            payload.setGpu(gpu);
        }

        if (metricSections.contains(MetricSection.DISKS) && diskMetrics != null) {
            payload.setDisks(
                    diskMetrics.stream().map(this::mapDisk).toList()
            );
        }

        return payload;
    }

    private DiskData mapDisk(DiskMetricsEntity diskMetrics) {
        DiskData disk = new DiskData();
        disk.setDevice(diskMetrics.getDevice());
        disk.setFilesystem(diskMetrics.getFilesystem());
        disk.setTotalSpace(diskMetrics.getTotalSpace());
        disk.setUsedSpace(diskMetrics.getUsedSpace());
        disk.setMountedOn(diskMetrics.getMountedOn());
        return disk;
    }

    private DashboardSnapshotPayload basePayload(UUID agentId, Long snapshotId, Instant capturedAt) {
        DashboardSnapshotPayload payload = new DashboardSnapshotPayload();
        payload.setAgentId(agentId.toString());
        payload.setSnapshotId(snapshotId);
        payload.setCapturedAt(capturedAt);
        return payload;
    }
}
