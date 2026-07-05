package dev.masnypen.gamepulseserver.models.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class DashboardSubscription {
    private Set<UUID> agentIds;
    private Set<MetricSection> metricSections;

    public boolean matchesAgent(UUID agentId) {
        return agentIds == null || agentIds.isEmpty() || agentIds.contains(agentId);
    }
}
