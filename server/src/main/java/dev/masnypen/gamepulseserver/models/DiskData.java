package dev.masnypen.gamepulseserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiskData {
    private String device;
    private String filesystem;
    private DiskType diskType;
    private double total_space;
    private double used_space;
    private String mounted_on;

    public enum DiskType {
        SSD,
        HDD;
    }
}
