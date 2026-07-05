package dev.masnypen.gamepulseserver.models.dashboard;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public enum MetricSection {
    SYSTEM,
    CPU,
    MEMORY,
    GPU,
    DISKS;

    public static EnumSet<MetricSection> all() {
        return EnumSet.allOf(MetricSection.class);
    }

    public static EnumSet<MetricSection> fromRawNames(Set<String> rawNames) {
        if (rawNames == null || rawNames.isEmpty()) {
            return all();
        }

        EnumSet<MetricSection> sections = EnumSet.noneOf(MetricSection.class);
        for (String rawName : rawNames) {
            if (rawName == null || rawName.isBlank()) {
                continue;
            }
            sections.add(MetricSection.valueOf(rawName.trim().toUpperCase(Locale.ROOT)));
        }
        return sections.isEmpty() ? all() : sections;
    }

    public static EnumSet<MetricSection> fromCsv(String rawCsv) {
        if (rawCsv == null || rawCsv.isBlank()) {
            return all();
        }
        Set<String> names = Arrays.stream(rawCsv.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toSet());
        return fromRawNames(names);
    }
}
