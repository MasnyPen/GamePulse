package dev.masnypen.gamepulseserver.models.metrics;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gpu_metrics")
public class GpuMetricsEntity {

    @Id
    private Long snapshotId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "snapshot_id")
    private SystemSnapshotEntity snapshot;

    private String vendor;
    private String model;
    private double totalVram;
    private double usedVram;
    private double loadPercentage;
    private double temperature;
}