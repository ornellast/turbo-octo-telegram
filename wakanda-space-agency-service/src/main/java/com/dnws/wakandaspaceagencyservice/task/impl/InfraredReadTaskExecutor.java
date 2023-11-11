package com.dnws.wakandaspaceagencyservice.task.impl;

import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;
import com.dnws.wakandaspaceagencyservice.reader.impl.InfraredReader;
import com.dnws.wakandaspaceagencyservice.task.ISatelliteReadTaskExecutor;
import jakarta.persistence.EntityNotFoundException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class InfraredReadTaskExecutor implements ISatelliteReadTaskExecutor {

    private UUID satelliteId;
    private SatelliteRepository repository;


    public InfraredReadTaskExecutor(UUID satelliteId, SatelliteRepository service) {
        this.satelliteId = satelliteId;
        this.repository = service;
    }

    @Override
    public UUID getSatelliteId() {
        return satelliteId;
    }

    @Override
    public SatelliteRepository getSatelliteRepository() {
        return this.repository;
    }

    @Override
    public void run() {

        Optional<SatelliteEntity> optional = repository.findById(satelliteId);

        optional.ifPresent(entity -> {
            var reader = new InfraredReader(entity.getZones());
            var data = reader.read();
            publish(data);
            entity.setLastReading(Instant.now());
            repository.save(entity);
        });

        if (optional.isEmpty()) {
            throw new EntityNotFoundException("[InfraredData] No satellite found with ID: " + satelliteId);
        }


    }

    private void publish(String weatherDataTopic) {
        System.out.println("[InfraredData] Publishing data: " + weatherDataTopic);
    }
}
