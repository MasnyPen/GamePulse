package dev.masnypen.gamepulseserver.repositories;

import dev.masnypen.gamepulseserver.models.metrics.GpuMetricsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GpuMetricsRepository extends JpaRepository<GpuMetricsEntity, Long> {
    Optional<GpuMetricsEntity> findBySnapshotId(Long snapshotId);
}
