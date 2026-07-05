package dev.masnypen.gamepulseserver.repositories;

import dev.masnypen.gamepulseserver.models.metrics.DiskMetricsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiskMetricsRepository extends JpaRepository<DiskMetricsEntity, Long> {
    List<DiskMetricsEntity> findAllBySnapshot_Id(Long snapshotId);
}
