package com.dnws.wakandaspaceagencyservice.service.impl;

import com.dnws.wakandaspaceagencyservice.TestUtils;
import com.dnws.wakandaspaceagencyservice.enums.SatelliteType;
import com.dnws.wakandaspaceagencyservice.model.Coordinate;
import com.dnws.wakandaspaceagencyservice.model.Frequency;
import com.dnws.wakandaspaceagencyservice.model.ScheduledData;
import com.dnws.wakandaspaceagencyservice.model.Zone;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;
import com.dnws.wakandaspaceagencyservice.service.IReadingSchedulerService;
import com.dnws.wakandaspaceagencyservice.task.ISatelliteReadTaskExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ReadingSchedulerServiceTest {

    private final SatelliteRepository repository = mock();

    private final IReadingSchedulerService service = new ReadingSchedulerService(repository);
    private final ScheduledExecutorService executorService = mock();
    private Map<UUID, ScheduledData> scheduledReadings;
    private Map<UUID, Set<UUID>> scheduledSatellites;

    @BeforeEach
    void cleanMaps() {
        scheduledReadings = spy(new ConcurrentHashMap<>());
        scheduledSatellites = spy(new ConcurrentHashMap<>());
        ReflectionTestUtils.setField(service, "scheduledReadings", scheduledReadings);
        ReflectionTestUtils.setField(service, "scheduledSatellites", scheduledSatellites);
        ReflectionTestUtils.setField(service, "executorService", executorService);
    }

    @Test
    void cancelReading_shouldDoNothing_when_ScheduleIsNotFound() {
        // Given
        var id = UUID.randomUUID();

        // When
        service.cancelReading(id);

        // Then
        verifyNoInteractions(scheduledSatellites);

    }

    @Test
    void cancelReading_shouldCancelScheduleAndRemoveItFromScheduledSatelliteSet_when_ScheduleIsFound() {
        // Given
        var satelliteId = UUID.randomUUID();
        var scheduleId = UUID.randomUUID();

        ScheduledFuture schedule = mock();

        Set<UUID> setOfScheduleIds = spy(new HashSet<>());
        setOfScheduleIds.add(scheduleId);

        ScheduledData scheduleData = mock();

        scheduledReadings.put(scheduleId, scheduleData);
        scheduledSatellites.put(satelliteId, setOfScheduleIds);

        when(scheduleData.schedule()).thenReturn(schedule);
        when(scheduleData.satelliteId()).thenReturn(satelliteId);
        when(schedule.cancel(false)).thenReturn(true);

        // When
        service.cancelReading(scheduleId);

        // Then
        verify(scheduledReadings).get(scheduleId);
        verify(schedule).cancel(false);
        verify(scheduledSatellites).get(satelliteId);
        verify(setOfScheduleIds).remove(scheduleId);


    }

    @Test
    void schedule_shouldReturnNull_when_SatelliteIsNotFound() {
        // given
        var id = UUID.randomUUID();
        var entity = TestUtils.createEntity(id);
        when(repository.findById(eq(id))).thenReturn(Optional.empty());

        // When
        UUID scheduleId = service.schedule(entity);

        assertNull(scheduleId);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "MINUTES,3,WEATHER",
            "SECONDS,60,WEATHER",
            "HOURS,1,INFRARED"
    })
    void schedule_shouldReturnScheduleId_when_createAScheduleAtFixedRateAccordinglyWithFrequencyAndUpdateTheMaps(
            TimeUnit unit,
            Long value,
            SatelliteType type
    ) {
        // Given
        UUID id = UUID.randomUUID();
        SatelliteEntity entity = TestUtils.createEntity(id);
        Frequency frequency = new Frequency(unit, value);
        entity.setReadingFrequency(frequency);
        entity.setType(type);
        ScheduledFuture schedule = mock();

        when(repository.findById(eq(id))).thenReturn(Optional.of(entity));
        when(executorService.scheduleAtFixedRate(
                any(ISatelliteReadTaskExecutor.class),
                anyLong(),
                eq(frequency.value()),
                eq(frequency.unit())
        ))
                .thenReturn(schedule);

        // When
        UUID actual = service.schedule(entity);

        //Then
        verify(scheduledReadings).put(any(), any());
        verify(scheduledSatellites).compute(eq(id), any());
        assertTrue(actual != null);
    }

    @Test
    void cancelAllScheduledReadings_shouldDoNothing_when_thereIsNoSatelliteReadingScheduled(){
        // Given
        UUID satelliteId =UUID.randomUUID();

        //When
        service.cancelAllScheduledReadings(satelliteId);

        //Then
        verify(scheduledSatellites).get(eq(satelliteId));
        verifyNoMoreInteractions(scheduledReadings);
    }

    @Test
    void cancelAllScheduledReadings_shouldCancelAllReadings_when_SatelliteReadingScheduledIsFound(){
        // Given
        UUID satelliteId =UUID.randomUUID();
        UUID scheduledReadingId1 = UUID.randomUUID();
        UUID scheduledReadingId2 = UUID.randomUUID();
        UUID scheduledReadingId3 = UUID.randomUUID();
        ScheduledFuture<?> schedule1 =  mock();
        ScheduledFuture<?> schedule2 =  mock();
        ScheduledFuture<?> schedule3 =  mock();
        ScheduledData scheduledData1 = new ScheduledData(satelliteId,schedule1);
        ScheduledData scheduledData2 = new ScheduledData(satelliteId,schedule2);
        ScheduledData scheduledData3 = new ScheduledData(satelliteId,schedule3);

        Set<UUID> setScheduledReadings = new HashSet<>(Set.of(scheduledReadingId1, scheduledReadingId2, scheduledReadingId3));

        scheduledSatellites.put(satelliteId, setScheduledReadings);
        scheduledReadings.put(scheduledReadingId1, scheduledData1);
        scheduledReadings.put(scheduledReadingId2, scheduledData2);
        scheduledReadings.put(scheduledReadingId3, scheduledData3);

        when(schedule1.cancel(eq(true))).thenReturn(true);
        when(schedule2.cancel(eq(true))).thenReturn(true);
        when(schedule3.cancel(eq(true))).thenReturn(true);

        //When
        service.cancelAllScheduledReadings(satelliteId);

        //Then
        verify(scheduledSatellites).get(eq(satelliteId));
        verify(scheduledReadings).get(eq(scheduledReadingId1));
        verify(scheduledReadings).get(eq(scheduledReadingId2));
        verify(scheduledReadings).get(eq(scheduledReadingId3));
        verify(scheduledReadings).remove(eq(scheduledReadingId1));
        verify(scheduledReadings).remove(eq(scheduledReadingId2));
        verify(scheduledReadings).remove(eq(scheduledReadingId3));
    }
}