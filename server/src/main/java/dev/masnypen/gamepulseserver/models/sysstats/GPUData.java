package dev.masnypen.gamepulseserver.models.sysstats;

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
    private long totalVram;
    @SerializedName("used_vram")
    private long usedVram;
    @SerializedName("load_percentage")
    private double loadPercentage;
    private double temperature;
}
