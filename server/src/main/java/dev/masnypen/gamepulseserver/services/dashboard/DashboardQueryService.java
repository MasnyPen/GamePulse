package dev.masnypen.gamepulseserver.services.dashboard;

import dev.masnypen.gamepulseserver.models.agents.GPAgent;
import dev.masnypen.gamepulseserver.models.dashboard.DashboardAgentDto;
import dev.masnypen.gamepulseserver.models.dashboard.DashboardSnapshotPayload;
import dev.masnypen.gamepulseserver.models.dashboard.MetricSection;
import dev.masnypen.gamepulseserver.models.metrics.SystemSnapshotEntity;
import dev.masnypen.gamepulseserver.repositories.*;
import dev.masnypen.gamepulseserver.services.SessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardQueryService {

    private final GPAgentRepository agentRepository;
    private final SystemSnapshotRepository snapshotRepository;
    private final SystemDataRepository systemDataRepository;
    private final CpuMetricsRepository cpuMetricsRepository;
    private final MemoryMetricsRepository memoryMetricsRepository;
    private final GpuMetricsRepository gpuMetricsRepository;
    private final DiskMetricsRepository diskMetricsRepository;
    private final SessionRegistry sessionRegistry;
    private final DashboardSnapshotMapper mapper;

    @Transactional(readOnly = true)
    public List<DashboardAgentDto> getAgents() {
        return agentRepository.findAll().stream()
                .sorted(Comparator.comparing(agent -> Optional.ofNullable(agent.getHostname()).orElse(agent.getId().toString())))
                .map(this::toAgentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DashboardSnapshotPayload> getLatestSnapshots(Set<UUID> requestedAgentIds, Set<MetricSection> metricSections) {
        List<GPAgent> agents = requestedAgentIds == null || requestedAgentIds.isEmpty()
                ? agentRepository.findAll()
                : agentRepository.findAllById(requestedAgentIds);

        List<DashboardSnapshotPayload> snapshots = new ArrayList<>();

        for (GPAgent agent : agents) {
            Optional<SystemSnapshotEntity> latest = snapshotRepository.findTopByAgent_IdOrderByCapturedAtDesc(agent.getId());
            if (latest.isEmpty()) {
                continue;
            }

            SystemSnapshotEntity snapshot = latest.get();
            Long snapshotId = snapshot.getId();

            snapshots.add(
                    mapper.fromEntities(
                            snapshot,
                            systemDataRepository.findBySnapshotId(snapshotId).orElse(null),
                            cpuMetricsRepository.findBySnapshotId(snapshotId).orElse(null),
                            memoryMetricsRepository.findBySnapshotId(snapshotId).orElse(null),
                            gpuMetricsRepository.findBySnapshotId(snapshotId).orElse(null),
                            diskMetricsRepository.findAllBySnapshot_Id(snapshotId),
                            metricSections
                    )
            );
        }

        snapshots.sort(Comparator.comparing(DashboardSnapshotPayload::getCapturedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return snapshots;
    }

    private DashboardAgentDto toAgentDto(GPAgent agent) {
        return new DashboardAgentDto(
                agent.getId().toString(),
                agent.getHostname(),
                agent.getLastIp(),
                agent.getLastAuth(),
                sessionRegistry.isConnected(agent.getId())
        );
    }
}
