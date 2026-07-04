package dev.masnypen.gamepulseserver.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GPUData {
    private String vendor;
    private String model;
    @SerializedName("total_vram")
    private double totalVram;
    @SerializedName("used_vram")
    private double usedVram;
    @SerializedName("load_percentage")
    private double loadPercentage;
    private double temperature;
}
