package com.dnws.wakandaspaceagencyservice.service.impl;

import com.dnws.wakandaspaceagencyservice.kafka.model.WeatherDataTopic;
import com.dnws.wakandaspaceagencyservice.kafka.publisher.IPublisher;
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Service
public class ReadingSchedulerService implements IReadingSchedulerService {

    private final SatelliteRepository repository;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Map<UUID, ScheduledData> scheduledReadings = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> scheduledSatellites = new ConcurrentHashMap<>();

    private final IPublisher<WeatherDataTopic, UUID> weatherTopicPublisher;
    private final IPublisher<String, String> infraredTopicPublisher;

    public ReadingSchedulerService(SatelliteRepository repository, IPublisher<WeatherDataTopic, UUID> weatherTopicPublisher, IPublisher<String, String> infraredTopicPublisher) {
        this.repository = repository;
        this.weatherTopicPublisher = weatherTopicPublisher;
        this.infraredTopicPublisher = infraredTopicPublisher;
    }


    @Override
    public UUID schedule(SatelliteEntity externalEntity) {

        Optional<SatelliteEntity> optional = repository.findById(externalEntity.getId());

        if (optional.isEmpty()) {
            return null;
        }
        SatelliteEntity entity = optional.get();

        var frequency = entity.getReadingFrequency();

        ISatelliteReadTaskExecutor taskExecutor = getTaskExecutor(entity);

        ScheduledFuture<?> schedule = executorService.scheduleAtFixedRate(taskExecutor, 2, frequency.value(), frequency.unit());

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

    @Override
    public ISatelliteReadTaskExecutor getTaskExecutor(SatelliteEntity entity) {
        return switch (entity.getType()) {
            case INFRARED -> new InfraredReadTaskExecutor(entity.getId(), repository, infraredTopicPublisher);
            default -> new WeatherReadTaskExecutor(entity.getId(), repository, weatherTopicPublisher);
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

    @Override
    public void cancelAllScheduledReadings(UUID satelliteId) {
        Set<UUID> scheduledSatellites = this.scheduledSatellites
                .get(satelliteId);

        if (scheduledSatellites != null && !scheduledSatellites.isEmpty()) {
            scheduledSatellites
                    .forEach(scheduleId -> {
                        ScheduledData scheduledData = scheduledReadings.get(scheduleId);
                        if (scheduledData != null) {
                            scheduledData.schedule().cancel(true);
                            scheduledReadings.remove(scheduleId);
                        }
                    });
            scheduledSatellites.remove(satelliteId);
        }
    }
}
