package com.dnws.wakandaspaceagencyservice.service;

import com.dnws.wakandaspaceagencyservice.model.Frequency;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISatelliteService {
    void executeReading(UUID satelliteId);

    boolean activate(UUID satelliteId);

    boolean deactivate(UUID satelliteId);

    Optional<SatelliteEntity> save(SatelliteEntity satellite);

    boolean decommission(UUID satelliteId);

    boolean updateReadingFrequency(UUID satelliteId, Frequency frequency);

    List<SatelliteEntity> findAll();
}
