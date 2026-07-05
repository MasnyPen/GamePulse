package dev.masnypen.gamepulseserver.models.sysstats;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CPUData {
    private String name;
    private int threads;
    private double temperature;
    private int frequency;
    @SerializedName("used_percentage")
    private double usedPercentage;
}
