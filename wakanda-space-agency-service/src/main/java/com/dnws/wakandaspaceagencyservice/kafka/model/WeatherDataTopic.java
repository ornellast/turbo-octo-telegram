package com.dnws.wakandaspaceagencyservice.kafka.model;

import com.dnws.wakandaspaceagencyservice.enums.WindRose;
import com.dnws.wakandaspaceagencyservice.model.WeatherData;
import com.dnws.wakandaspaceagencyservice.model.Zone;

import java.util.UUID;

public record WeatherDataTopic(
        UUID satelliteId,

        UUID readingId,

        Double temperature,

        Double windSpeed,

        WindRose windDirection,

        Zone zone,

        Long timestamp

) {
    public WeatherDataTopic(
            UUID satelliteId,
            WeatherData weatherData
    ) {
        this(
                satelliteId,
                weatherData.readingId(),
                weatherData.temperature(),
                weatherData.windSpeed(),
                weatherData.windDirection(),
                weatherData.zone(),
                weatherData.timestamp()
        );
    }
}
