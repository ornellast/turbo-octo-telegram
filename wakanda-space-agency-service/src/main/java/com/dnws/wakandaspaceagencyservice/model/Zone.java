package com.dnws.wakandaspaceagencyservice.model;

import org.springframework.util.Assert;

import java.util.UUID;

public record Zone(Coordinates topLeftCoordinate, Coordinates bottomRightCoordinate, UUID id) {

    public Zone {
        Assert.notNull(topLeftCoordinate, "topLeftCoordinate cannot be null");
        Assert.notNull(bottomRightCoordinate, "bottomRightCoordinate cannot be null");
    }
    public Zone(Coordinates topLeftCoordinate, Coordinates bottomRightCoordinate){
        this(topLeftCoordinate, bottomRightCoordinate, UUID.randomUUID());
    }
}
