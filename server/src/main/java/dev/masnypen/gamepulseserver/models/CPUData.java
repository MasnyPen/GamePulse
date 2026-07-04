package dev.masnypen.gamepulseserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CPUData {
    private int threads;
    private int temperature;
    private double used_percentage;
}
