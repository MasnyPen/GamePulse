package dev.masnypen.gamepulseserver.models.sysstats;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemData {
    private String name;
    private String kernel;
    @SerializedName("os_version")
    private String osVersion;
    private String hostname;
}
