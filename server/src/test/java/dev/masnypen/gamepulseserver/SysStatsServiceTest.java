package dev.masnypen.gamepulseserver;

import dev.masnypen.gamepulseserver.factories.TestSysStatsFactory;
import dev.masnypen.gamepulseserver.models.GPAgent;
import dev.masnypen.gamepulseserver.models.sysstats.SysStats;
import dev.masnypen.gamepulseserver.repositories.*;
import dev.masnypen.gamepulseserver.services.SessionRegistry;
import dev.masnypen.gamepulseserver.services.SysStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysStatsServiceTest {

    @Mock SystemSnapshotRepository snapshotRepo;
    @Mock CpuMetricsRepository cpuRepo;
    @Mock DiskMetricsRepository diskRepo;
    @Mock GpuMetricsRepository gpuRepo;
    @Mock MemoryMetricsRepository memoryRepo;
    @Mock SystemDataRepository systemDataRepo;
    @Mock GPAgentRepository gpAgentRepository;
    @Mock SessionRegistry sessionRegistry;

    @Mock WebSocketSession session;

    @InjectMocks
    SysStatsService service;

    private UUID agentId;

    @BeforeEach
    void setUp() {
        agentId = UUID.randomUUID();
    }

    @Test
    void shouldSaveFullSysStatsFlow() {
        // given
        when(sessionRegistry.getAgentId(session)).thenReturn(agentId);

        GPAgent agent = new GPAgent();
        when(gpAgentRepository.findById(agentId))
                .thenReturn(Optional.of(agent));

        SysStats stats = TestSysStatsFactory.create();

        when(snapshotRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        service.handle(session, stats);

        // then - snapshot created
        verify(snapshotRepo, times(1)).save(any());

        // system data saved
        verify(systemDataRepo, times(1)).save(any());

        // cpu saved
        verify(cpuRepo, times(1)).save(any());

        // memory saved
        verify(memoryRepo, times(1)).save(any());

        // gpu saved
        verify(gpuRepo, times(1)).save(any());

        // disks saved
        verify(diskRepo, times(1)).saveAll(anyList());
    }
}