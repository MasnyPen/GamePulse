package dev.masnypen.gamepulseserver.repositories;

import dev.masnypen.gamepulseserver.models.GPAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GPAgentRepository extends JpaRepository<GPAgent, UUID> {

    GPAgent getGPAgentById(UUID uuid);
}
