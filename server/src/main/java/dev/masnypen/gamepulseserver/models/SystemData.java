package dev.masnypen.gamepulseserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemData {
    private String name;
    private String kernel;
    private String os_version;
    private String host_name;
}
