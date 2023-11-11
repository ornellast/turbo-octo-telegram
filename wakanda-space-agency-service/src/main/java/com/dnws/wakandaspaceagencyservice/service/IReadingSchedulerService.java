package com.dnws.wakandaspaceagencyservice.service;

import com.dnws.wakandaspaceagencyservice.model.ScheduledData;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;

import java.util.UUID;

public interface IReadingSchedulerService {

    UUID schedule(SatelliteEntity entity);

    void cancelReading(UUID scheduleId);

    void cancelAllScheduledReadings(UUID satelliteId);

}
