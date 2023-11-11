package com.dnws.wakandaspaceagencyservice.model;

import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

public record Frequency(TimeUnit unit, Long value) {

    public Frequency {
        Assert.notNull(unit, "unit cannot be null");
        Assert.notNull(value, "value cannot be null");
        Assert.isTrue(value > 0, "value has to be greater than 0");
    }
}
