package dev.masnypen.gamepulseserver.models.metrics;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "memory_metrics")
public class MemoryMetricsEntity {

    @Id
    private Long snapshotId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "snapshot_id")
    private SystemSnapshotEntity snapshot;

    private double total;
    private double used;

    @Column(name = "total_swap")
    private double totalSwap;

    @Column(name = "used_swap")
    private double usedSwap;
}