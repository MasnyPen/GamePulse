package dev.masnypen.gamepulseserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SysStats {
    private SystemData system;
    private MemoryData memory;
    private CPUData cpu;
    private GPUData gpu;
    private List<DiskData> disks;
}
