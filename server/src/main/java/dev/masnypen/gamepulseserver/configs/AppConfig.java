package dev.masnypen.gamepulseserver.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String auth_token;
}