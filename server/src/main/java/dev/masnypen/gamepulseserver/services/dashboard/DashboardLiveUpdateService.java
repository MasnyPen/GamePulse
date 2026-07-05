package dev.masnypen.gamepulseserver.services.dashboard;

import com.google.gson.Gson;
import dev.masnypen.gamepulseserver.models.dashboard.DashboardLiveMessage;
import dev.masnypen.gamepulseserver.models.dashboard.DashboardSnapshotPayload;
import dev.masnypen.gamepulseserver.models.dashboard.DashboardSubscription;
import dev.masnypen.gamepulseserver.models.dashboard.MetricSection;
import dev.masnypen.gamepulseserver.models.sysstats.SysStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class DashboardLiveUpdateService {

    private static final String LIVE_UPDATE_TYPE = "live_update";
    private final DashboardSnapshotMapper mapper;
    private final Gson gson = new Gson();

    private final Map<WebSocketSession, DashboardSubscription> subscriptions = new ConcurrentHashMap<>();

    public void register(WebSocketSession session) {
        subscriptions.put(session, new DashboardSubscription(Set.of(), MetricSection.all()));
    }

    public void unregister(WebSocketSession session) {
        subscriptions.remove(session);
    }

    public void setFilter(WebSocketSession session, Set<UUID> agentIds, Set<MetricSection> metricSections) {
        Set<UUID> selectedAgents = agentIds == null ? Set.of() : Set.copyOf(agentIds);
        Set<MetricSection> selectedMetrics = (metricSections == null || metricSections.isEmpty())
                ? MetricSection.all()
                : Set.copyOf(metricSections);

        subscriptions.put(session, new DashboardSubscription(selectedAgents, selectedMetrics));
    }

    public DashboardSubscription getSubscription(WebSocketSession session) {
        return subscriptions.get(session);
    }

    public void broadcast(UUID agentId, Long snapshotId, Instant capturedAt, SysStats stats) {
        for (Map.Entry<WebSocketSession, DashboardSubscription> entry : subscriptions.entrySet()) {
            WebSocketSession session = entry.getKey();
            DashboardSubscription subscription = entry.getValue();

            if (!session.isOpen() || !subscription.matchesAgent(agentId)) {
                continue;
            }

            DashboardSnapshotPayload payload = mapper.fromSysStats(
                    agentId,
                    snapshotId,
                    capturedAt,
                    stats,
                    subscription.getMetricSections()
            );

            try {
                session.sendMessage(new TextMessage(gson.toJson(new DashboardLiveMessage(LIVE_UPDATE_TYPE, payload))));
            } catch (IOException e) {
                subscriptions.remove(session);
            }
        }
    }
}
