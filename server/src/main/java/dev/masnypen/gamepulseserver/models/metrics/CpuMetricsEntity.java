package dev.masnypen.gamepulseserver.models.metrics;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cpu_metrics")
public class CpuMetricsEntity {

    @Id
    private Long snapshotId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "snapshot_id")
    private SystemSnapshotEntity snapshot;

    private String name;
    private int threads;
    private double temperature;
    private int frequency;
    private double usedPercentage;
}