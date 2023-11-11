package com.dnws.wakandaspaceagencyservice.task.impl;

import com.dnws.wakandaspaceagencyservice.kafka.model.WeatherDataTopic;
import com.dnws.wakandaspaceagencyservice.model.WeatherData;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;
import com.dnws.wakandaspaceagencyservice.reader.ISatelliteDataReader;
import com.dnws.wakandaspaceagencyservice.reader.impl.WeatherReader;
import com.dnws.wakandaspaceagencyservice.task.ISatelliteReadTaskExecutor;

import java.time.Instant;
import java.util.List;

public class WeatherReadTaskExecutor implements ISatelliteReadTaskExecutor {

    private SatelliteEntity entity;
    private SatelliteRepository repository;

    public WeatherReadTaskExecutor (SatelliteEntity satellite, SatelliteRepository service)
    {
        this.entity = satellite;
        this.repository = service;
    }

    @Override
    public SatelliteEntity getSatelliteEntity() {
        return entity;
    }

    @Override
    public SatelliteRepository getSatelliteRepository() {
        return repository;
    }

    @Override
    public void run() {
        ISatelliteDataReader<List<WeatherData>> reader = new WeatherReader(entity.getZones());
        List<WeatherData> weatherDataList = reader.read();

        weatherDataList.forEach(weatherData -> {
            publish(new WeatherDataTopic(entity.getId(), weatherData));
        });

        entity.setLastReading(Instant.now());
        repository.save(entity);

    }

    private void publish(WeatherDataTopic weatherDataTopic) {
        System.out.println("Publishing data: " + weatherDataTopic);
    }
}
