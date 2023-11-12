package com.dnws.wakandaspaceagencyservice.task.impl;

import com.dnws.wakandaspaceagencyservice.kafka.model.WeatherDataTopic;
import com.dnws.wakandaspaceagencyservice.kafka.publisher.IPublisher;
import com.dnws.wakandaspaceagencyservice.model.WeatherData;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;
import com.dnws.wakandaspaceagencyservice.reader.ISatelliteDataReader;
import com.dnws.wakandaspaceagencyservice.reader.impl.WeatherReader;
import com.dnws.wakandaspaceagencyservice.task.ISatelliteReadTaskExecutor;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WeatherReadTaskExecutor implements ISatelliteReadTaskExecutor<WeatherDataTopic, UUID> {

    private UUID satelliteId;
    private SatelliteRepository repository;
    private IPublisher<WeatherDataTopic, UUID> publisher;

    public WeatherReadTaskExecutor(UUID satelliteId, SatelliteRepository repository, IPublisher<WeatherDataTopic, UUID> publisher) {
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
        return repository;
    }

    @Override
    public IPublisher<WeatherDataTopic, UUID> getPublisher() {
        return this.publisher;
    }

    @Override
    public void run() {
        Optional<SatelliteEntity> optional = repository.findById(satelliteId);

        optional.ifPresent(entity -> {
            ISatelliteDataReader<List<WeatherData>> reader = new WeatherReader(entity.getZones());
            List<WeatherData> weatherDataList = reader.read();

            weatherDataList.forEach(weatherData -> {
                WeatherDataTopic weatherDataTopic = new WeatherDataTopic(satelliteId, weatherData);
                publisher.publish(weatherDataTopic.readingId(), weatherDataTopic);
            });

            entity.setLastReading(Instant.now());
            repository.save(entity);
        });

        if (optional.isEmpty()) {
            throw new EntityNotFoundException("[WeatherReadTaskExecutor] No satellite found with ID: " + satelliteId);
        }
    }
}
