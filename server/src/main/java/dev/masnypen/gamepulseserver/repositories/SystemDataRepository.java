package dev.masnypen.gamepulseserver.repositories;

import dev.masnypen.gamepulseserver.models.metrics.SystemDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemDataRepository extends JpaRepository<SystemDataEntity, Long> {
    Optional<SystemDataEntity> findBySnapshotId(Long snapshotId);
}
