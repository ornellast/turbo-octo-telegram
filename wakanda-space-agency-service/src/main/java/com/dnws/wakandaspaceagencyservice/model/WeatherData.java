package com.dnws.wakandaspaceagencyservice.model;

import com.dnws.wakandaspaceagencyservice.enums.WindRose;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.UUID;

public record WeatherData(
        Double temperature,
        Double windSpeed,

        WindRose windDirection,

        Zone zone,

        Long timestamp,

        UUID readingId
) {
    public WeatherData {
        Assert.notNull(temperature, "temperature cannot be bull");
        Assert.notNull(windSpeed, "windSpeed cannot be bull");
        Assert.notNull(windDirection, "windDirection cannot be bull");
        Assert.notNull(zone, "zone cannot be bull");
    }

    public WeatherData(Double temperature, Double windSpeed, WindRose windDirection, Zone zone) {
        this(
                temperature,
                windSpeed,
                windDirection,
                zone,
                Instant.now().getEpochSecond(),
                UUID.randomUUID());
    }
}
