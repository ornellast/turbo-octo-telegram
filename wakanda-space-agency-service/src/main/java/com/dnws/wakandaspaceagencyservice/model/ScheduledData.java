package com.dnws.wakandaspaceagencyservice.model;

import org.springframework.util.Assert;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

public record ScheduledData(
        UUID satelliteId,
        ScheduledFuture<?> schedule,
        UUID id
) {
    public ScheduledData {
        Assert.notNull(satelliteId, "latitude cannot be null");
        Assert.notNull(schedule, "longitude cannot be null");
    }

    public ScheduledData(
            UUID satelliteId,
            ScheduledFuture<?> scheduled
    ) {
        this(satelliteId, scheduled, UUID.randomUUID());
    }
}
