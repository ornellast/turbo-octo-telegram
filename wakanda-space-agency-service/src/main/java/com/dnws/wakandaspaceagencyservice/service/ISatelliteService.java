package com.dnws.wakandaspaceagencyservice.service;

import com.dnws.wakandaspaceagencyservice.model.ReadingFrequency;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface ISatelliteService {
    void executeReading(UUID satelliteId);

    boolean activate(UUID satelliteId);

    boolean deactivate(UUID satelliteId);

    Optional<SatelliteEntity> save(SatelliteEntity satellite);

    boolean decommission(UUID satelliteId);

    boolean updateReadingFrequency(UUID satelliteId, ReadingFrequency frequency);
}
