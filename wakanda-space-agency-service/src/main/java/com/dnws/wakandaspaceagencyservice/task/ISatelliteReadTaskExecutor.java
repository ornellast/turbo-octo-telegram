package com.dnws.wakandaspaceagencyservice.task;

import com.dnws.wakandaspaceagencyservice.kafka.publisher.IPublisher;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;

import java.util.UUID;

public interface ISatelliteReadTaskExecutor<T, ID> extends Runnable {
    UUID getSatelliteId();

    SatelliteRepository getSatelliteRepository();

    IPublisher<T, ID> getPublisher();

    void run();
}
