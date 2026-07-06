package dev.masnypen.gamepulseserver.tasks;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dev.masnypen.gamepulseserver.configs.AppConfig;
import dev.masnypen.gamepulseserver.models.metrics.SystemSnapshotEntity;
import dev.masnypen.gamepulseserver.repositories.SystemSnapshotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OldSnapshotsDeleteTask {

    private final SystemSnapshotRepository snapshotRepository;
    private final AppConfig appConfig;
    
    @Scheduled(fixedRate = 1200000)
    @Transactional
    public void deleteOldSnapshots() {
        List<SystemSnapshotEntity> old = snapshotRepository.findByCapturedAtBefore(appConfig.getOldSnapshotsThresholdInstant());
        int count = old.size();

        snapshotRepository.deleteAll(old);
        System.out.println("Cleaning old snapshots. Snapshots deleted: " + count);
    }
}
