package dev.masnypen.gamepulseserver.models.sysstats;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoryData {
    private long total;
    private long used;
    @SerializedName("total_swap")
    private long totalSwap;
    @SerializedName("used_swap")
    private long usedSwap;
}
