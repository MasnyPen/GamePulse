package dev.masnypen.gamepulseserver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.masnypen.gamepulseserver.factories.TestSysStatsFactory;
import dev.masnypen.gamepulseserver.models.dashboard.MetricSection;
import dev.masnypen.gamepulseserver.models.sysstats.SysStats;
import dev.masnypen.gamepulseserver.services.dashboard.DashboardLiveUpdateService;
import dev.masnypen.gamepulseserver.services.dashboard.DashboardSnapshotMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardLiveUpdateServiceTest {

    private DashboardLiveUpdateService liveUpdateService;
    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        liveUpdateService = new DashboardLiveUpdateService(new DashboardSnapshotMapper());
        session = mock(WebSocketSession.class);
        when(session.isOpen()).thenReturn(true);
    }

    @Test
    void shouldSendOnlyRequestedMetricsForMatchingAgent() throws Exception {
        UUID expectedAgentId = UUID.randomUUID();
        SysStats stats = TestSysStatsFactory.create();

        liveUpdateService.register(session);
        liveUpdateService.setFilter(session, Set.of(expectedAgentId), Set.of(MetricSection.CPU));

        liveUpdateService.broadcast(expectedAgentId, 12L, Instant.now(), stats);

        ArgumentCaptor<TextMessage> textCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(textCaptor.capture());

        JsonObject payload = JsonParser.parseString(textCaptor.getValue().getPayload()).getAsJsonObject();
        JsonObject snapshot = payload.getAsJsonObject("payload");

        assertEquals("live_update", payload.get("type").getAsString());
        assertTrue(snapshot.has("cpu"));
        assertFalse(snapshot.has("memory"));
        assertFalse(snapshot.has("gpu"));
        assertFalse(snapshot.has("system"));
        assertFalse(snapshot.has("disks"));
    }

    @Test
    void shouldSkipNonMatchingAgent() throws Exception {
        UUID configuredAgent = UUID.randomUUID();
        UUID otherAgent = UUID.randomUUID();
        SysStats stats = TestSysStatsFactory.create();

        liveUpdateService.register(session);
        liveUpdateService.setFilter(session, Set.of(configuredAgent), Set.of(MetricSection.CPU));

        liveUpdateService.broadcast(otherAgent, 13L, Instant.now(), stats);

        verify(session, never()).sendMessage(any());
    }
}
