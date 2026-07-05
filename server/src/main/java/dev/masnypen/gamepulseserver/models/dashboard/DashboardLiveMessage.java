package dev.masnypen.gamepulseserver.models.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardLiveMessage {
    private String type;
    private DashboardSnapshotPayload payload;
}
