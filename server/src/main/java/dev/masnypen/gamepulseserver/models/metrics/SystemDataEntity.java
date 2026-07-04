package dev.masnypen.gamepulseserver.models.metrics;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "system_data")
public class SystemDataEntity {

    @Id
    private Long snapshotId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "snapshot_id")
    private SystemSnapshotEntity snapshot;

    private String name;
    private String kernel;

    @Column(name = "os_version")
    private String osVersion;

    private String hostname;
}