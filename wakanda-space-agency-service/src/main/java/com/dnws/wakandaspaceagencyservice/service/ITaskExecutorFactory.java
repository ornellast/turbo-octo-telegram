package com.dnws.wakandaspaceagencyservice.service;

import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.task.ISatelliteReadTaskExecutor;

public interface ITaskExecutorFactory {
    ISatelliteReadTaskExecutor getTaskExecutor(SatelliteEntity entity);
}
