package com.dnws.wakandaspaceagencyservice.task;

import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;

public interface ISatelliteReadTaskExecutor extends Runnable {
    SatelliteEntity getSatelliteEntity();

    SatelliteRepository getSatelliteRepository();

    void run();
}
