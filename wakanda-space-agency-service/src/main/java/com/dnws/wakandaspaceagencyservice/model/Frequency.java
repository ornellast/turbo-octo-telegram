package com.dnws.wakandaspaceagencyservice.model;

import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

public record Frequency(TimeUnit unit, Integer value) {

    public Frequency {
        Assert.notNull(unit, "unit cannot be null");
        Assert.notNull(value, "value cannot be null");
    }
}
