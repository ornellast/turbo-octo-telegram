package com.dnws.wakandaspaceagencyservice.service;

import com.dnws.wakandaspaceagencyservice.model.ScheduledData;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.task.ISatelliteReadTaskExecutor;

import java.util.UUID;

public interface IReadingSchedulerService extends ITaskExecutorFactory {

    UUID schedule(SatelliteEntity entity);

    void cancelReading(UUID scheduleId);

    void cancelAllScheduledReadings(UUID satelliteId);

}
