package com.dnws.wakandaspaceagencyservice.task.impl;

import com.dnws.wakandaspaceagencyservice.kafka.publisher.IPublisher;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;
import com.dnws.wakandaspaceagencyservice.reader.impl.InfraredReader;
import com.dnws.wakandaspaceagencyservice.task.ISatelliteReadTaskExecutor;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class InfraredReadTaskExecutor implements ISatelliteReadTaskExecutor<String, String> {

    private UUID satelliteId;
    private SatelliteRepository repository;
    private IPublisher<String, String> publisher;


    public InfraredReadTaskExecutor(UUID satelliteId, SatelliteRepository repository, IPublisher<String, String> publisher) {
        Assert.notNull(satelliteId, "satelliteId cannot be null");
        Assert.notNull(repository, "repository cannot be null");
        Assert.notNull(publisher, "publisher cannot be null");
        this.satelliteId = satelliteId;
        this.repository = repository;
        this.publisher = publisher;
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
    public IPublisher<String, String> getPublisher() {
        return this.publisher;
    }

    @Override
    public void run() {

        Optional<SatelliteEntity> optional = repository.findById(satelliteId);

        optional.ifPresent(entity -> {
            var reader = new InfraredReader(entity.getZones());
            var data = reader.read();
            publisher.publish(data, data);
            entity.setLastReading(Instant.now());
            repository.save(entity);
        });

        if (optional.isEmpty()) {
            throw new EntityNotFoundException("[InfraredData] No satellite found with ID: " + satelliteId);
        }


    }
}
