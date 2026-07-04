package dev.masnypen.gamepulseserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysStats {
    private SystemData system;
    private MemoryData memory;
    private CPUData cpu;
    private GPUData gpu;
    private List<DiskData> disks;
}
