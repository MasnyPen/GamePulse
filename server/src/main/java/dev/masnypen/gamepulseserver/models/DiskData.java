package dev.masnypen.gamepulseserver.models;

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
    private double totalSpace;
    @SerializedName("used_space")
    private double usedSpace;
    @SerializedName("mounted_on")
    private String mountedOn;
}
