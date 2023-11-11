package com.dnws.wakandaspaceagencyservice.service.impl;

import com.dnws.wakandaspaceagencyservice.enums.SatelliteType;
import com.dnws.wakandaspaceagencyservice.model.Coordinates;
import com.dnws.wakandaspaceagencyservice.model.ReadingFrequency;
import com.dnws.wakandaspaceagencyservice.model.ScannedZone;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;
import com.dnws.wakandaspaceagencyservice.service.ISatelliteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SatelliteServiceTest {

    private final SatelliteRepository satelliteRepository = mock();

    @InjectMocks
    private ISatelliteService satelliteService = new SatelliteService(satelliteRepository);

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

    @ParameterizedTest
    @CsvSource(value = {
            ", 3, false",
            "MINUTES, , false",
            ", , false",
            "HOURS, 1, true",
    })
    void updateReadingFrequency_shouldReturnTheExpectedResult_accordinglyReadingFrequencyState(
            TimeUnit unit,
            Integer value,
            boolean expected
    ) {
        // given
        var id = UUID.randomUUID();
        var frequency = new ReadingFrequency(unit, value);
        var entity = spy(createEntity(id));

        if (expected) {
            when(satelliteRepository.findById(eq(id))).thenReturn(Optional.of(entity));
            entity.setReadingFrequency(frequency);
            when(satelliteRepository.save(eq(entity))).thenReturn(entity);
        }

        // when
        var actual = satelliteService.updateReadingFrequency(id, frequency);

        // then
        assertEquals(expected, actual);

    }

    @ParameterizedTest
    @CsvSource(value = {
            ", 3",
            "MINUTES, ",
            ", ",
    })
    void save_shouldReturnEmpty_when_frequencyIsInvalid(
            TimeUnit unit,
            Integer value
    ) {
        // given
        var id = UUID.randomUUID();
        var entity = spy(createEntity(id));
        entity.setReadingFrequency(new ReadingFrequency(unit, value));


        // when
        var actual = satelliteService.save(entity);

        // then
        assertFalse(actual.isPresent());
        verify(satelliteRepository, never()).save(any());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1.1, 2.2, 3.3,",
            "1.1, 2.2, ,4.4",
            "1.1, , 3.3,4.4",
            ", 2.2, 3.3, 4.4",
    })
    void save_shouldReturnEmpty_when_ScannedZonesIsInvalid(
            Double tlLat,
            Double tlLon,
            Double brLat,
            Double brLon

    ) {
        // given
        var id = UUID.randomUUID();
        var entity = spy(createEntity(id));
        var zone = new ScannedZone(
                new Coordinates(tlLat, tlLon),
                new Coordinates(brLat, brLon)
        );
        entity.setScannedZones(List.of(zone));


        // when
        var actual = satelliteService.save(entity);

        // then
        assertFalse(actual.isPresent());
        verify(satelliteRepository, never()).save(any());
    }

    @Test
    void save_shouldReturnPersistedNewSatellite_when_validationPasses(){
        // given
        UUID id = null;
        var entity = createEntity(id);

        when(satelliteRepository.save(eq(entity))).thenReturn(entity);

        // when
        Optional<SatelliteEntity> actual = satelliteService.save(entity);

        //then
        assertTrue(actual.isPresent());
        verify(satelliteRepository,never()).findById(any());
        verify(satelliteRepository).save(eq(entity));
    }

    @Test
    void save_shouldReturnUpdateSatellite_when_validationPasses(){
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
    }

    @Test
    void decommission() {
    }

    private SatelliteEntity createEntity(UUID id) {
        var entity = new SatelliteEntity();
        entity.setId(id);
        entity.setReadingFrequency(new ReadingFrequency(TimeUnit.MINUTES, 5));
        entity.setActive(true);
        entity.setType(SatelliteType.WEATHER);
        entity.setScannedZones(
                List.of(
                        new ScannedZone(
                                new Coordinates(Math.random() * 10, Math.random() * 100),
                                new Coordinates(Math.random() * 10, Math.random() * 100)
                        )
                )
        );

        return entity;
    }
}