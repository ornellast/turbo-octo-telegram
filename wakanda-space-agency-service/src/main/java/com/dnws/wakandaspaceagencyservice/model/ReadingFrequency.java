package com.dnws.wakandaspaceagencyservice.model;

import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

public record ReadingFrequency(TimeUnit unit, Integer value) {

    public ReadingFrequency {
        Assert.notNull(unit, "unit cannot be null");
        Assert.notNull(value, "value cannot be null");
    }
}
