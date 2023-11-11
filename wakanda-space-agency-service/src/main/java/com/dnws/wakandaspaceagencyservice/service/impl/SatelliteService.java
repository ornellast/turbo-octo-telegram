package com.dnws.wakandaspaceagencyservice.service.impl;

import com.dnws.wakandaspaceagencyservice.model.Coordinate;
import com.dnws.wakandaspaceagencyservice.model.Frequency;
import com.dnws.wakandaspaceagencyservice.model.Zone;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;
import com.dnws.wakandaspaceagencyservice.service.IReadingSchedulerService;
import com.dnws.wakandaspaceagencyservice.service.ISatelliteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SatelliteService implements ISatelliteService {

    private final SatelliteRepository repository;

    private final IReadingSchedulerService scheduler;

    public SatelliteService(SatelliteRepository repository, IReadingSchedulerService scheduler) {
        this.repository = repository;
        this.scheduler = scheduler;
    }

    @Override
    public List<SatelliteEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public void executeReading(UUID satelliteId) {
        throw new RuntimeException("Not implemented yet!");
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

        if (satellite.getId() != null) {
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
        // should cancel any schedule scanning
        return false;
    }


    @Override
    public boolean updateReadingFrequency(UUID satelliteId, Frequency frequency) {
        if (isNotValidReadingFrequency(frequency)) {
            return false;
        }
        Optional<SatelliteEntity> optional = repository.findById(satelliteId);

        if (optional.isEmpty()) {
            decommission(satelliteId);
            return false;
        }

        var entity = optional.get();

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
