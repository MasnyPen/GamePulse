package dev.masnypen.gamepulseserver.models.sysstats;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiskData {
    private String device;
    private String filesystem;
    @SerializedName("total_space")
    private long totalSpace;
    @SerializedName("used_space")
    private long usedSpace;
    @SerializedName("mounted_on")
    private String mountedOn;
}
