package dev.masnypen.gamepulseserver;

import dev.masnypen.gamepulseserver.models.agents.GPAgent;
import dev.masnypen.gamepulseserver.models.dashboard.DashboardSnapshotPayload;
import dev.masnypen.gamepulseserver.models.dashboard.MetricSection;
import dev.masnypen.gamepulseserver.models.metrics.CpuMetricsEntity;
import dev.masnypen.gamepulseserver.models.metrics.SystemSnapshotEntity;
import dev.masnypen.gamepulseserver.repositories.*;
import dev.masnypen.gamepulseserver.services.SessionRegistry;
import dev.masnypen.gamepulseserver.services.dashboard.DashboardQueryService;
import dev.masnypen.gamepulseserver.services.dashboard.DashboardSnapshotMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DashboardQueryServiceTest {

    @Mock
    GPAgentRepository agentRepository;
    @Mock
    SystemSnapshotRepository snapshotRepository;
    @Mock
    SystemDataRepository systemDataRepository;
    @Mock
    CpuMetricsRepository cpuMetricsRepository;
    @Mock
    MemoryMetricsRepository memoryMetricsRepository;
    @Mock
    GpuMetricsRepository gpuMetricsRepository;
    @Mock
    DiskMetricsRepository diskMetricsRepository;
    @Mock
    SessionRegistry sessionRegistry;

    private DashboardQueryService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new DashboardQueryService(
                agentRepository,
                snapshotRepository,
                systemDataRepository,
                cpuMetricsRepository,
                memoryMetricsRepository,
                gpuMetricsRepository,
                diskMetricsRepository,
                sessionRegistry,
                new DashboardSnapshotMapper()
        );
    }

    @Test
    void shouldReturnLatestSnapshotWithSelectedMetrics() {
        UUID agentId = UUID.randomUUID();
        GPAgent agent = new GPAgent(agentId, "host", "127.0.0.1", System.currentTimeMillis());
        SystemSnapshotEntity snapshot = new SystemSnapshotEntity(10L, Instant.now(), agent);

        CpuMetricsEntity cpu = new CpuMetricsEntity();
        cpu.setSnapshot(snapshot);
        cpu.setName("Ryzen");
        cpu.setThreads(12);
        cpu.setTemperature(66.0);
        cpu.setFrequency(4100);
        cpu.setUsedPercentage(45.0);

        when(agentRepository.findAllById(Set.of(agentId))).thenReturn(List.of(agent));
        when(snapshotRepository.findTopByAgent_IdOrderByCapturedAtDesc(agentId)).thenReturn(Optional.of(snapshot));
        when(cpuMetricsRepository.findBySnapshotId(10L)).thenReturn(Optional.of(cpu));
        when(systemDataRepository.findBySnapshotId(10L)).thenReturn(Optional.empty());
        when(memoryMetricsRepository.findBySnapshotId(10L)).thenReturn(Optional.empty());
        when(gpuMetricsRepository.findBySnapshotId(10L)).thenReturn(Optional.empty());
        when(diskMetricsRepository.findAllBySnapshot_Id(10L)).thenReturn(List.of());

        List<DashboardSnapshotPayload> result = service.getLatestSnapshots(Set.of(agentId), Set.of(MetricSection.CPU));

        assertEquals(1, result.size());
        assertNotNull(result.getFirst().getCpu());
        assertNull(result.getFirst().getMemory());
        assertNull(result.getFirst().getGpu());
        assertNull(result.getFirst().getSystem());
        assertNull(result.getFirst().getDisks());
    }
}
