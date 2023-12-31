package com.dnws.wakandaspaceagencyservice.service.impl;

import com.dnws.wakandaspaceagencyservice.model.Coordinate;
import com.dnws.wakandaspaceagencyservice.model.Frequency;
import com.dnws.wakandaspaceagencyservice.model.Zone;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;
import com.dnws.wakandaspaceagencyservice.service.IReadingSchedulerService;
import com.dnws.wakandaspaceagencyservice.service.ISatelliteService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SatelliteService implements ISatelliteService {

    private final SatelliteRepository repository;

    private final IReadingSchedulerService scheduler;

    @PersistenceContext
    private EntityManager entityManager;

    public SatelliteService(SatelliteRepository repository, IReadingSchedulerService scheduler, EntityManager entityManager) {
        this.repository = repository;
        this.scheduler = scheduler;
        this.entityManager = entityManager;
    }

    @Override
    public List<SatelliteEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<SatelliteEntity> findById(UUID satelliteId) {
        return repository.findById(satelliteId);
    }

    @Override
    public void readNow(UUID satelliteId) {
        repository.findById(satelliteId).ifPresent(entity -> {
            scheduler.getTaskExecutor(entity).run();
        });
    }

    @Override
    public boolean activate(UUID satelliteId) {
        return setSatelliteActivationStatus(satelliteId, true);
    }

    @Override
    public boolean deactivate(UUID satelliteId) {
        return setSatelliteActivationStatus(satelliteId, false);
    }

    @Override
    public Optional<SatelliteEntity> save(SatelliteEntity satellite) {
        if (
                !areValidZones(satellite.getZones()) ||
                        isNotValidReadingFrequency(satellite.getReadingFrequency())
        ) {
            return Optional.empty();
        }

        if (satellite.getId() != null && !entityManager.contains(satellite)) {
            repository.findById(satellite.getId()).ifPresent(managed -> {
                satellite.setVersion(managed.getVersion());
            });
        }

        SatelliteEntity managedEntity = repository.save(satellite);

        scheduler.schedule(managedEntity);

        return Optional.of(managedEntity);
    }

    @Override
    public boolean decommission(UUID satelliteId) {
        Optional<SatelliteEntity> optional = repository.findById(satelliteId);

        if (optional.isEmpty()) {
            return false;
        }

        SatelliteEntity entity = optional.get();
        if(!entity.isActive()){
            return false;
        }
        scheduler.cancelAllScheduledReadings(satelliteId);

        repository.delete(entity);
        return true;

    }


    @Override
    public boolean updateReadingFrequency(UUID satelliteId, Frequency frequency) {
        if (isNotValidReadingFrequency(frequency)) {
            return false;
        }
        Optional<SatelliteEntity> optional = repository.findById(satelliteId);

        if (optional.isEmpty()) {
            return false;
        }

        var entity = optional.get();

        scheduler.cancelAllScheduledReadings(entity.getId());

        entity.setReadingFrequency(frequency);

        return save(entity).isPresent();
    }

    private boolean isNotValidReadingFrequency(Frequency frequency) {
        return frequency == null ||
                frequency.unit() == null ||
                frequency.value() == null ||
                frequency.value() <= 0;
    }

    private boolean areValidZones(List<Zone> zones) {
        return zones != null &&
                !zones.isEmpty() &&
                zones.stream().allMatch(this::isValidZone);
    }

    private boolean isValidZone(Zone zone) {
        return zone != null &&
                isValidCoordinate(zone.topLeftCoordinate()) &&
                isValidCoordinate(zone.bottomRightCoordinate());
    }

    private boolean isValidCoordinate(Coordinate coordinate) {
        return coordinate != null &&
                coordinate.latitude() != null &&
                coordinate.longitude() != null;
    }

    private boolean setSatelliteActivationStatus(UUID satelliteId, boolean status) {

        var optional = repository
                .findById(satelliteId);

        if (optional.isEmpty()) {
            return false;
        }

        var satelliteEntity = optional.get();
        satelliteEntity.setActive(status);
        repository.save(satelliteEntity);
        return true;

    }
}
