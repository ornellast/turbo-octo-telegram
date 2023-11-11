package com.dnws.wakandaspaceagencyservice.model;

import org.springframework.util.Assert;

import java.util.UUID;

public record ScannedZone(Coordinates topLeftCoordinate, Coordinates bottomRightCoordinate, UUID id) {

    public ScannedZone {
        Assert.notNull(topLeftCoordinate, "topLeftCoordinate cannot be null");
        Assert.notNull(bottomRightCoordinate, "bottomRightCoordinate cannot be null");
    }
    public ScannedZone(Coordinates topLeftCoordinate, Coordinates bottomRightCoordinate){
        this(topLeftCoordinate, bottomRightCoordinate, UUID.randomUUID());
    }
}
