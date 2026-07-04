package dev.masnypen.gamepulseserver.models.metrics;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "disk_metrics")
public class DiskMetricsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snapshot_id")
    private SystemSnapshotEntity snapshot;

    private String device;
    private String filesystem;
    private double totalSpace;
    private double usedSpace;
    private String mountedOn;
}