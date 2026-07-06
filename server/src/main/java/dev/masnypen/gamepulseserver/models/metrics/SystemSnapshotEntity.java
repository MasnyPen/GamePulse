package dev.masnypen.gamepulseserver.models.metrics;

import dev.masnypen.gamepulseserver.models.agents.GPAgent;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "system_snapshot")
public class SystemSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant capturedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agent_id", nullable = false)
    private GPAgent agent;

    @OneToOne(mappedBy = "snapshot", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private CpuMetricsEntity cpuMetrics;

    @OneToOne(mappedBy = "snapshot", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private GpuMetricsEntity gpuMetrics;

    @OneToOne(mappedBy = "snapshot", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private MemoryMetricsEntity memoryMetrics;

    @OneToOne(mappedBy = "snapshot", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private SystemDataEntity systemData;

    @OneToMany(mappedBy = "snapshot", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<DiskMetricsEntity> diskMetrics;

}