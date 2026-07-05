package dev.masnypen.gamepulseserver.controllers;

import dev.masnypen.gamepulseserver.models.dashboard.DashboardAgentDto;
import dev.masnypen.gamepulseserver.models.dashboard.DashboardSnapshotPayload;
import dev.masnypen.gamepulseserver.models.dashboard.MetricSection;
import dev.masnypen.gamepulseserver.services.dashboard.DashboardQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardQueryService dashboardQueryService;

    @GetMapping("/agents")
    public List<DashboardAgentDto> getAgents() {
        return dashboardQueryService.getAgents();
    }

    @GetMapping("/snapshots/latest")
    public List<DashboardSnapshotPayload> getLatestSnapshots(
            @RequestParam(value = "agentIds", required = false) String rawAgentIds,
            @RequestParam(value = "metrics", required = false) String rawMetrics
    ) {
        return dashboardQueryService.getLatestSnapshots(parseAgentIds(rawAgentIds), MetricSection.fromCsv(rawMetrics));
    }

    private Set<UUID> parseAgentIds(String rawAgentIds) {
        if (rawAgentIds == null || rawAgentIds.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(rawAgentIds.split(","))
                .map(String::trim)
                .filter(id -> !id.isBlank())
                .map(UUID::fromString)
                .collect(Collectors.toSet());
    }
}
