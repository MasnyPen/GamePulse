package dev.masnypen.gamepulseserver.repositories;

import dev.masnypen.gamepulseserver.models.metrics.CpuMetricsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CpuMetricsRepository extends JpaRepository<CpuMetricsEntity, Long> {
    Optional<CpuMetricsEntity> findBySnapshotId(Long snapshotId);
}
