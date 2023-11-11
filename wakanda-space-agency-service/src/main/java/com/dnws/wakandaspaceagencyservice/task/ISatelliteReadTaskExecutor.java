package com.dnws.wakandaspaceagencyservice.task;

import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;

import java.util.UUID;

public interface ISatelliteReadTaskExecutor extends Runnable {
    UUID getSatelliteId();

    SatelliteRepository getSatelliteRepository();

    void run();
}
