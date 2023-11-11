package com.dnws.wakandaspaceagencyservice.model;

import org.springframework.util.Assert;

public record Coordinates(Double latitude, Double longitude) {
    public Coordinates {
        Assert.notNull(latitude, "latitude cannot be null");
        Assert.notNull(longitude, "longitude cannot be null");
    }
}
