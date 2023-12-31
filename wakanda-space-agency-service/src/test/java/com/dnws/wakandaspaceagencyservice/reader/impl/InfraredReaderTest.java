package com.dnws.wakandaspaceagencyservice.reader.impl;

import com.dnws.wakandaspaceagencyservice.TestUtils;
import com.dnws.wakandaspaceagencyservice.model.Coordinate;
import com.dnws.wakandaspaceagencyservice.model.WeatherData;
import com.dnws.wakandaspaceagencyservice.model.Zone;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InfraredReaderTest {


    @Test
    void constructor_shouldNotAcceptNullZones() {
        // Given
        List<Zone> zones = null;

        assertThrows(IllegalArgumentException.class, () -> new InfraredReader(zones));
    }

    @Test
    void constructor_shouldNotAcceptEmptyZones() {
        // Given
        List<Zone> zones = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> new InfraredReader(zones));
    }

    @Test
    void read_shouldProduceOutput() {
        // Given
        Zone zone1 = TestUtils.createZone();
        Zone zone2 = TestUtils.createZone();
        Zone zone3 = TestUtils.createZone();
        var zones = new ArrayList<>(List.of(zone1, zone2, zone3));

        var reader = new InfraredReader(zones);

        // When
        String actual = reader.read();

        // Then
        assertNotNull(actual);
    }
}