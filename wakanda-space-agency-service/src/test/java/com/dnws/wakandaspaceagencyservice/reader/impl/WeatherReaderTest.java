package com.dnws.wakandaspaceagencyservice.reader.impl;

import com.dnws.wakandaspaceagencyservice.TestUtils;
import com.dnws.wakandaspaceagencyservice.model.Coordinate;
import com.dnws.wakandaspaceagencyservice.model.WeatherData;
import com.dnws.wakandaspaceagencyservice.model.Zone;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeatherReaderTest {


    @Test
    void constructor_shouldNotAcceptNullZones() {
        // Given
        List<Zone> zones = null;

        assertThrows(IllegalArgumentException.class, () -> new WeatherReader(zones));
    }

    @Test
    void constructor_shouldNotAcceptEmptyZones() {
        // Given
        List<Zone> zones = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> new WeatherReader(zones));
    }

    @Test
    void read_shouldCreateOneWeatherDataForEachZone() {
        // Given
        Zone zone1 = TestUtils.createZone();
        Zone zone2 = TestUtils.createZone();
        Zone zone3 = TestUtils.createZone();
        var zones = new ArrayList<>(List.of(zone1, zone2, zone3));

        var reader = new WeatherReader(zones);

        // When
        List<WeatherData> weatherDataList = reader.read();

        // Then
        assertArrayEquals(
                zones.stream().map(zone -> zone.id()).toArray(),
                weatherDataList.stream().map(weatherData -> weatherData.zone().id()).toArray()
        );
    }
}