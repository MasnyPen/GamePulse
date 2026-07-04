package dev.masnypen.gamepulseserver.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoryData {
    private double total;
    private double used;
    @SerializedName("total_swap")
    private double totalSwap;
    @SerializedName("used_swap")
    private double usedSwap;
}
