package dev.masnypen.gamepulseserver.models.dashboard;

import dev.masnypen.gamepulseserver.models.sysstats.CPUData;
import dev.masnypen.gamepulseserver.models.sysstats.DiskData;
import dev.masnypen.gamepulseserver.models.sysstats.GPUData;
import dev.masnypen.gamepulseserver.models.sysstats.MemoryData;
import dev.masnypen.gamepulseserver.models.sysstats.SystemData;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class DashboardSnapshotPayload {
    private String agentId;
    private Long snapshotId;
    private Instant capturedAt;
    private SystemData system;
    private CPUData cpu;
    private MemoryData memory;
    private GPUData gpu;
    private List<DiskData> disks;
}
