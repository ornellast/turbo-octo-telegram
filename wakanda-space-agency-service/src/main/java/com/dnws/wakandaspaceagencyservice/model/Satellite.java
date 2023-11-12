package com.dnws.wakandaspaceagencyservice.model;

import com.dnws.wakandaspaceagencyservice.enums.SatelliteType;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record Satellite(
        UUID id,

        String name,

        Frequency readingFrequency,

        boolean isActive,

        SatelliteType type,


        List<Zone> zones,

        Instant lastReading
) {

    public Satellite(String name,
                     Frequency readingFrequency,
                     boolean isActive,
                     SatelliteType type,
                     List<Zone> zones
    ) {
        this(null, name, readingFrequency, isActive, type, zones, null);
    }

    public static Satellite from(SatelliteEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Satellite(
                entity.getId(),
                entity.getName(),
                entity.getReadingFrequency(),
                entity.isActive(),
                entity.getType(),
                entity.getZones(),
                entity.getLastReading()
        );
    }

    public static List<Satellite> from(List<SatelliteEntity> entityList) {
        if (entityList == null) {
            return new ArrayList<>();
        }

        return entityList.stream().map(Satellite::from).toList();
    }

    public SatelliteEntity toEntity() {
        var zonesWithId =  zones.stream().map(z -> {
            if(z.id() == null){
                return new Zone( z.topLeftCoordinate(), z.bottomRightCoordinate() );
            }
            return z;
        }).toList();
        return new SatelliteEntity(
                null,name, readingFrequency, isActive, type, zonesWithId, null
        );
    }
}
