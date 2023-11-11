package com.dnws.wakandaspaceagencyservice.reader.impl;

import com.dnws.wakandaspaceagencyservice.enums.WindRose;
import com.dnws.wakandaspaceagencyservice.model.WeatherData;
import com.dnws.wakandaspaceagencyservice.model.Zone;
import com.dnws.wakandaspaceagencyservice.reader.ISatelliteDataReader;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Random;

public class WeatherReader implements ISatelliteDataReader<List<WeatherData>> {

    private List<Zone> zones;

    public WeatherReader(List<Zone> zones) {
        Assert.notNull(zones, "zones cannot be null");
        Assert.notEmpty(zones, "zones cannot be empty");
        this.zones = zones;
    }

    @Override
    public List<WeatherData> read() {
        return zones.stream()
                .map(zone -> {
                    return new WeatherData(
                            Math.random() * 10,
                            Math.random() * 10,
                            WindRose.values()[(new Random()).nextInt(WindRose.values().length)],
                            zone);

                }).toList();
    }
}
