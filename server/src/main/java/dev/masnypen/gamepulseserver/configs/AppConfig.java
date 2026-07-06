package dev.masnypen.gamepulseserver.configs;

import lombok.Data;

import java.time.Duration;
import java.time.Instant;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String auth_token;

    private String old_snapshots_threshold;

    public Instant getOldSnapshotsThresholdInstant() {
        return parseDurationToInstant(old_snapshots_threshold);
    }

    private Instant parseDurationToInstant(String value) {
        if (value == null) {
            return Instant.now();
        }

        value = value.trim().toLowerCase();

        long number;
        if (value.endsWith("d")) {
            number = Long.parseLong(value.replace("d", ""));
            return Instant.now().minus(Duration.ofDays(number));
        }

        if (value.endsWith("h")) {
            number = Long.parseLong(value.replace("h", ""));
            return Instant.now().minus(Duration.ofHours(number));
        }

        if (value.endsWith("m")) {
            number = Long.parseLong(value.replace("m", ""));
            return Instant.now().minus(Duration.ofMinutes(number));
        }

        if (value.endsWith("s")) {
            number = Long.parseLong(value.replace("s", ""));
            return Instant.now().minus(Duration.ofSeconds(number));
        }

        throw new IllegalArgumentException("Invalid duration format: " + value);
    }
}