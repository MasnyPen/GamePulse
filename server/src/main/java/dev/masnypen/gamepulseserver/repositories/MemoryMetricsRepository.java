package dev.masnypen.gamepulseserver.repositories;

import dev.masnypen.gamepulseserver.models.metrics.MemoryMetricsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemoryMetricsRepository extends JpaRepository<MemoryMetricsEntity, Long> {
    Optional<MemoryMetricsEntity> findBySnapshotId(Long snapshotId);
}
