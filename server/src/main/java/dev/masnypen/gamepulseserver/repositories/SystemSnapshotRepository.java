package dev.masnypen.gamepulseserver.repositories;

import dev.masnypen.gamepulseserver.models.metrics.SystemSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SystemSnapshotRepository extends JpaRepository<SystemSnapshotEntity, Long> {
    Optional<SystemSnapshotEntity> findTopByAgent_IdOrderByCapturedAtDesc(UUID agentId);
}
