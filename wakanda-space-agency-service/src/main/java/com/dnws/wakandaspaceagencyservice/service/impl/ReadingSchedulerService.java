package com.dnws.wakandaspaceagencyservice.service.impl;

import com.dnws.wakandaspaceagencyservice.model.ScheduledData;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;
import com.dnws.wakandaspaceagencyservice.service.IReadingSchedulerService;
import com.dnws.wakandaspaceagencyservice.task.ISatelliteReadTaskExecutor;
import com.dnws.wakandaspaceagencyservice.task.impl.InfraredReadTaskExecutor;
import com.dnws.wakandaspaceagencyservice.task.impl.WeatherReadTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ReadingSchedulerService implements IReadingSchedulerService {

    private final SatelliteRepository repository;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private Map<UUID, ScheduledData> scheduledReadings = new ConcurrentHashMap<>();
    private Map<UUID, Set<UUID>> scheduledSatellites = new ConcurrentHashMap<>();

    public ReadingSchedulerService(SatelliteRepository repository) {
        this.repository = repository;
    }


    @Override
    public UUID schedule(SatelliteEntity entity) {
        var frequency = entity.getReadingFrequency();

        ISatelliteReadTaskExecutor taskExecutor = getTaskExecutor(entity);

        ScheduledFuture<?> schedule = executorService.scheduleAtFixedRate(taskExecutor, 2, frequency.value(), frequency.unit());


//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//        ScheduledFuture<?> schedule = executorService.schedule(taskExecutor, 5, TimeUnit.SECONDS);

        UUID satelliteId = entity.getId();
        ScheduledData scheduleData = new ScheduledData(satelliteId, schedule);
        UUID scheduleId = scheduleData.id();

        scheduledReadings.put(scheduleId, scheduleData);
        scheduledSatellites.compute(satelliteId, (key, val) -> {
            if (val == null) {
                val = new HashSet<>();
            }
            val.add(scheduleId);
            return val;
        });
        return scheduleId;
    }

    private ISatelliteReadTaskExecutor getTaskExecutor(SatelliteEntity entity) {
        return switch (entity.getType()) {
            case INFRARED -> new InfraredReadTaskExecutor(entity, repository);
            default -> new WeatherReadTaskExecutor(entity, repository);
        };
    }

    @Override
    public void cancelReading(UUID scheduleId) {
        var scheduledData = scheduledReadings.get(scheduleId);
        if (scheduledData != null) {
            scheduledData.schedule().cancel(false);
            var scheduleSet = scheduledSatellites.get(scheduledData.satelliteId());
            scheduleSet.remove(scheduleId);
        }
    }
}
