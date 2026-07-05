package dev.masnypen.gamepulseserver.models.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardAgentDto {
    private String id;
    private String hostname;
    private String lastIp;
    private Long lastAuth;
    private boolean online;
}
