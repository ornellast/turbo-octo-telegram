package com.dnws.wakandaspaceagencyservice.task.impl;

import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;
import com.dnws.wakandaspaceagencyservice.reader.impl.InfraredReader;
import com.dnws.wakandaspaceagencyservice.task.ISatelliteReadTaskExecutor;

import java.time.Instant;

public class InfraredReadTaskExecutor implements ISatelliteReadTaskExecutor {

    private SatelliteEntity entity;
    private SatelliteRepository repository;


    public InfraredReadTaskExecutor(SatelliteEntity entity, SatelliteRepository repository) {
        this.entity = entity;
        this.repository = repository;
    }

    @Override
    public SatelliteEntity getSatelliteEntity() {
        return this.entity;
    }
    @Override
    public SatelliteRepository getSatelliteRepository() {
        return this.repository;
    }

    @Override
    public void run() {
        var reader = new InfraredReader(entity.getZones());
        var data = reader.read();


        publish(data);
        entity.setLastReading(Instant.now());
        repository.save(entity);
    }

    private void publish(String weatherDataTopic) {
        System.out.println("[InfraredData] Publishing data: " + weatherDataTopic);
    }
}
