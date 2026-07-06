package dev.masnypen.gamepulseserver.services;

import dev.masnypen.gamepulseserver.models.agents.GPAgent;
import dev.masnypen.gamepulseserver.models.metrics.*;
import dev.masnypen.gamepulseserver.models.sysstats.SysStats;
import dev.masnypen.gamepulseserver.repositories.*;
import dev.masnypen.gamepulseserver.services.dashboard.DashboardLiveUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SysStatsService {

    private final SystemSnapshotRepository snapshotRepo;
    private final CpuMetricsRepository cpuRepo;
    private final DiskMetricsRepository diskMetricsRepository;
    private final GpuMetricsRepository gpuMetricsRepository;
    private final MemoryMetricsRepository memoryMetricsRepository;
    private final SystemDataRepository systemDataRepository;
    private final GPAgentRepository gpAgentRepository;

    private final SessionRegistry sessionRegistry;
    private final DashboardLiveUpdateService dashboardLiveUpdateService;


    public void handle(WebSocketSession session, SysStats data) {
        save(data, sessionRegistry.getAgentId(session));
    }

    @Transactional
    public void save(SysStats stats, UUID agentId) {
        Instant capturedAt = Instant.now();

        GPAgent agent = gpAgentRepository.findById(agentId)
                        .orElseThrow();

        // SNAPSHOT (root)
        SystemSnapshotEntity snap = new SystemSnapshotEntity();
        snap.setCapturedAt(capturedAt);
        snap.setAgent(agent);

        SystemSnapshotEntity savedSnapshot = snapshotRepo.save(snap);

        // SYSTEM DATA
        SystemDataEntity system = new SystemDataEntity();
        system.setSnapshot(savedSnapshot);
        system.setName(stats.getSystem().getName());
        system.setKernel(stats.getSystem().getKernel());
        system.setOsVersion(stats.getSystem().getOsVersion());
        system.setHostname(stats.getSystem().getHostname());

        systemDataRepository.save(system);

        // CPU
        CpuMetricsEntity cpu = new CpuMetricsEntity();
        cpu.setSnapshot(savedSnapshot);
        cpu.setName(stats.getCpu().getName());
        cpu.setThreads(stats.getCpu().getThreads());
        cpu.setTemperature(stats.getCpu().getTemperature());
        cpu.setFrequency(stats.getCpu().getFrequency());
        cpu.setUsedPercentage(stats.getCpu().getUsedPercentage());

        cpuRepo.save(cpu);

        // MEMORY
        MemoryMetricsEntity mem = new MemoryMetricsEntity();
        mem.setSnapshot(savedSnapshot);
        mem.setTotal(stats.getMemory().getTotal());
        mem.setUsed(stats.getMemory().getUsed());
        mem.setTotalSwap(stats.getMemory().getTotalSwap());
        mem.setUsedSwap(stats.getMemory().getUsedSwap());

        memoryMetricsRepository.save(mem);

        // GPU
        GpuMetricsEntity gpu = new GpuMetricsEntity();
        gpu.setSnapshot(savedSnapshot);
        gpu.setVendor(stats.getGpu().getVendor());
        gpu.setModel(stats.getGpu().getModel());
        gpu.setTotalVram(stats.getGpu().getTotalVram());
        gpu.setUsedVram(stats.getGpu().getUsedVram());
        gpu.setLoadPercentage(stats.getGpu().getLoadPercentage());
        gpu.setTemperature(stats.getGpu().getTemperature());

        gpuMetricsRepository.save(gpu);

        // DISKS
        List<DiskMetricsEntity> disks = new ArrayList<>();

        stats.getDisks().forEach(d -> {
            DiskMetricsEntity disk = new DiskMetricsEntity();
            disk.setSnapshot(savedSnapshot);
            disk.setDevice(d.getDevice());
            disk.setFilesystem(d.getFilesystem());
            disk.setTotalSpace(d.getTotalSpace());
            disk.setUsedSpace(d.getUsedSpace());
            disk.setMountedOn(d.getMountedOn());

            disks.add(disk);
        });
        diskMetricsRepository.saveAll(disks);

        dashboardLiveUpdateService.broadcast(agentId, savedSnapshot.getId(), capturedAt, stats);
    }
}