package com.dnws.wakandaspaceagencyservice.task.impl;

import com.dnws.wakandaspaceagencyservice.kafka.model.WeatherDataTopic;
import com.dnws.wakandaspaceagencyservice.model.WeatherData;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;
import com.dnws.wakandaspaceagencyservice.reader.ISatelliteDataReader;
import com.dnws.wakandaspaceagencyservice.reader.impl.WeatherReader;
import com.dnws.wakandaspaceagencyservice.task.ISatelliteReadTaskExecutor;
import jakarta.persistence.EntityNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WeatherReadTaskExecutor implements ISatelliteReadTaskExecutor {

    private UUID satelliteId;
    private SatelliteRepository repository;

    public WeatherReadTaskExecutor(UUID satelliteId, SatelliteRepository service) {
        this.satelliteId = satelliteId;
        this.repository = service;
    }

    @Override
    public UUID getSatelliteId() {
        return satelliteId;
    }

    @Override
    public SatelliteRepository getSatelliteRepository() {
        return repository;
    }

    @Override
    public void run() {
        Optional<SatelliteEntity> optional = repository.findById(satelliteId);

        optional.ifPresent(entity -> {
            ISatelliteDataReader<List<WeatherData>> reader = new WeatherReader(entity.getZones());
            List<WeatherData> weatherDataList = reader.read();

            weatherDataList.forEach(weatherData -> {
                publish(new WeatherDataTopic(satelliteId, weatherData));
            });

            entity.setLastReading(Instant.now());
            repository.save(entity);
        });

        if (optional.isEmpty()) {
            throw new EntityNotFoundException("[WeatherReadTaskExecutor] No satellite found with ID: " + satelliteId);
        }
    }

    private void publish(WeatherDataTopic weatherDataTopic) {
        System.out.println("[WeatherReadTaskExecutor] Publishing data: " + weatherDataTopic);
    }
}
