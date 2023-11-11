package com.dnws.wakandaspaceagencyservice.model;

import org.springframework.util.Assert;

public record Coordinate(Double latitude, Double longitude) {
    public Coordinate {
        Assert.notNull(latitude, "latitude cannot be null");
        Assert.notNull(longitude, "longitude cannot be null");
    }
}
