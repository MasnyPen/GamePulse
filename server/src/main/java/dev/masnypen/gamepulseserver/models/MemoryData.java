package dev.masnypen.gamepulseserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemoryData {
    private double total;
    private double used;
    private double total_swap;
    private double used_swap;
}
