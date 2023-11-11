package com.dnws.wakandaspaceagencyservice.service.impl;

import com.dnws.wakandaspaceagencyservice.enums.SatelliteType;
import com.dnws.wakandaspaceagencyservice.model.Coordinate;
import com.dnws.wakandaspaceagencyservice.model.Frequency;
import com.dnws.wakandaspaceagencyservice.model.Zone;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;
import com.dnws.wakandaspaceagencyservice.service.IReadingSchedulerService;
import com.dnws.wakandaspaceagencyservice.service.ISatelliteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SatelliteServiceTest {

    private final SatelliteRepository satelliteRepository = mock();
    private final IReadingSchedulerService scheduler = mock();

    @InjectMocks
    private ISatelliteService satelliteService = new SatelliteService(satelliteRepository, scheduler);

    @Test
    void activate_shouldReturnFalse_when_satelliteIsNotFound() {
        // given
        var id = UUID.randomUUID();
        when(satelliteRepository.findById(eq(id))).thenReturn(Optional.empty());

        // when
        var result = satelliteService.activate(id);

        // then
        assertFalse(result);
        verify(satelliteRepository).findById(eq(id));
        verifyNoMoreInteractions(satelliteRepository);

    }


    @Test
    void activate_shouldSetActiveTrueAndReturnTrue_when_satelliteIsFound() {
        // given
        var id = UUID.randomUUID();
        var entity = spy(createEntity(id));
        entity.setId(id);
        when(satelliteRepository.findById(eq(id))).thenReturn(Optional.of(entity));

        // when
        var result = satelliteService.activate(id);

        // then
        assertTrue(result);
        verify(satelliteRepository).findById(eq(id));
        verify(satelliteRepository).save(eq(entity));
        verify(entity).setActive(true);

    }

    @Test
    void deactivate_shouldReturnFalse_when_satelliteIsNotFound() {
        // given
        var id = UUID.randomUUID();
        when(satelliteRepository.findById(eq(id))).thenReturn(Optional.empty());

        // when
        var result = satelliteService.deactivate(id);

        // then
        assertFalse(result);
        verify(satelliteRepository).findById(eq(id));
        verifyNoMoreInteractions(satelliteRepository);

    }


    @Test
    void deactivate_shouldSetActiveFalseAndReturnTrue_when_satelliteIsFound() {
        // given
        var id = UUID.randomUUID();
        var entity = spy(createEntity(id));

        when(satelliteRepository.findById(eq(id))).thenReturn(Optional.of(entity));

        // when
        var result = satelliteService.deactivate(id);

        // then
        assertTrue(result);
        verify(satelliteRepository).findById(eq(id));
        verify(satelliteRepository).save(eq(entity));
        verify(entity).setActive(false);

    }

    @Test
    void save_shouldReturnEmpty_when_FrequencyIsNull() {
        // given
        var id = UUID.randomUUID();
        var entity = createEntity(id);
        entity.setReadingFrequency(null);

        // when
        Optional<SatelliteEntity> actual = satelliteService.save(entity);

        //then
        assertTrue(actual.isEmpty());
        verifyNoMoreInteractions(satelliteRepository);
    }

    @Test
    void save_shouldReturnEmpty_when_ZonesIsNull() {
        // given
        var id = UUID.randomUUID();
        var entity = createEntity(id);
        entity.setZones(null);

        // when
        Optional<SatelliteEntity> actual = satelliteService.save(entity);

        //then
        assertTrue(actual.isEmpty());
        verifyNoMoreInteractions(satelliteRepository);
    }

    @Test
    void save_shouldReturnEmpty_when_ZonesIsEmpty() {
        // given
        var id = UUID.randomUUID();
        var entity = createEntity(id);
        entity.setZones(new ArrayList<>());

        // when
        Optional<SatelliteEntity> actual = satelliteService.save(entity);

        //then
        assertTrue(actual.isEmpty());
        verifyNoMoreInteractions(satelliteRepository);
    }

    @Test
    void save_shouldReturnPersistedNewSatellite_when_validationPasses() {
        // given
        var entity = createEntity(null);

        when(satelliteRepository.save(eq(entity))).thenReturn(entity);

        // when
        Optional<SatelliteEntity> actual = satelliteService.save(entity);

        //then
        assertTrue(actual.isPresent());
        verify(satelliteRepository, never()).findById(any());
        verify(satelliteRepository).save(eq(entity));
        verify(scheduler).schedule(eq(entity));
    }

    @Test
    void save_shouldReturnUpdateSatellite_when_validationPasses() {
        // given
        var id = UUID.randomUUID();
        var entity = createEntity(id);

        when(satelliteRepository.findById(eq(id))).thenReturn(Optional.of(entity));
        when(satelliteRepository.save(eq(entity))).thenReturn(entity);

        // when
        Optional<SatelliteEntity> actual = satelliteService.save(entity);

        //then
        assertTrue(actual.isPresent());
        verify(satelliteRepository).findById(eq(id));
        verify(satelliteRepository).save(eq(entity));
        verify(scheduler).schedule(eq(entity));
    }

    @Test
    void decommission_shouldReturnFalse_when_satelliteIsNotFound() {
        // given
        var id = UUID.randomUUID();

        when(satelliteRepository.findById(eq(id))).thenReturn(Optional.empty());

        // when
        boolean actual = satelliteService.decommission(id);

        //then
        assertFalse(actual);
        verify(satelliteRepository).findById(eq(id));
        verify(satelliteRepository, never()).delete(any());
    }

    @Test
    void decommission_shouldReturnFalse_when_satelliteIsNotActive() {
        // given
        var id = UUID.randomUUID();
        var entity = createEntity(id);
        entity.setActive(false);

        when(satelliteRepository.findById(eq(id))).thenReturn(Optional.of(entity));

        // when
        boolean actual = satelliteService.decommission(id);

        //then
        assertFalse(actual);
        verify(satelliteRepository).findById(eq(id));
        verify(satelliteRepository, never()).delete(any());
        verifyNoInteractions(scheduler);
    }

    @Test
    void decommission_shouldReturnTrue_when_satelliteIsActive() {
        // given
        var id = UUID.randomUUID();
        var entity = createEntity(id);

        when(satelliteRepository.findById(eq(id))).thenReturn(Optional.of(entity));

        // when
        boolean actual = satelliteService.decommission(id);

        //then
        assertTrue(actual);
        verify(satelliteRepository).findById(eq(id));
        verify(satelliteRepository).delete(eq(entity));
        verify(scheduler).cancelAllScheduledReadings(eq(id));
    }

    @Test
    void updateReadingFrequency_shouldReturnFalse_when_frequencyIsNull() {
        // given
        var id = UUID.randomUUID();

        // when
        boolean actual = satelliteService.updateReadingFrequency(id, null);

        //then
        assertFalse(actual);
        verifyNoMoreInteractions(satelliteRepository);
    }

    @Test
    void updateReadingFrequency_shouldReturnFalse_when_SatelliteIsNotFound() {
        // given
        var id = UUID.randomUUID();

        when(satelliteRepository.findById(eq(id))).thenReturn(Optional.empty());

        // when
        boolean actual = satelliteService.updateReadingFrequency(id, new Frequency(TimeUnit.SECONDS, 3L));

        //then
        assertFalse(actual);
        verify(satelliteRepository).findById(eq(id));
        verifyNoMoreInteractions(satelliteRepository);
    }

    @Test
    void updateReadingFrequency_shouldReturnTrueAndDoSchedule_when_SatelliteIsFoundAndFrequencyIsValid() {
        // given
        var id = UUID.randomUUID();
        var entity = spy(createEntity(id));
        Frequency frequency = new Frequency(TimeUnit.SECONDS, 3L);

        when(satelliteRepository.findById(eq(id))).thenReturn(Optional.of(entity));
        when(satelliteRepository.save(eq(entity))).thenReturn(entity);

        // when
        boolean actual = satelliteService.updateReadingFrequency(id, frequency);

        //then
        assertTrue(actual);
        verify(satelliteRepository, times(2)).findById(eq(id));
        verify(satelliteRepository).save(eq(entity));
        verify(scheduler).schedule(eq(entity));
    }

    private SatelliteEntity createEntity(UUID id) {
        var entity = new SatelliteEntity();
        entity.setId(id);
        entity.setReadingFrequency(new Frequency(TimeUnit.MINUTES, 5L));
        entity.setActive(true);
        entity.setType(SatelliteType.WEATHER);
        entity.setZones(
                List.of(
                        new Zone(
                                new Coordinate(Math.random() * 10, Math.random() * 100),
                                new Coordinate(Math.random() * 10, Math.random() * 100)
                        )
                )
        );

        return entity;
    }
}