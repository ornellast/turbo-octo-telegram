package com.dnws.wakandaspaceagencyservice;

import com.dnws.wakandaspaceagencyservice.enums.SatelliteType;
import com.dnws.wakandaspaceagencyservice.model.Coordinate;
import com.dnws.wakandaspaceagencyservice.model.Frequency;
import com.dnws.wakandaspaceagencyservice.model.Zone;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TestUtils {

    private TestUtils(){}

    public static SatelliteEntity createEntity(UUID id) {
        var entity = new SatelliteEntity();
        entity.setName(UUID.randomUUID().toString());
        entity.setId(id);
        entity.setReadingFrequency(new Frequency(TimeUnit.MINUTES, 5L));
        entity.setActive(true);
        entity.setType(SatelliteType.WEATHER);
        entity.setZones(
                List.of(createZone())
        );

        return entity;
    }

    public static Zone createZone() {
        var entity = new Zone(
                createCoordinate(),
                createCoordinate()
        );
        return entity;
    }

    public static Coordinate createCoordinate() {
        return new Coordinate(Math.random() * 10, Math.random() * 100);
    }
}
