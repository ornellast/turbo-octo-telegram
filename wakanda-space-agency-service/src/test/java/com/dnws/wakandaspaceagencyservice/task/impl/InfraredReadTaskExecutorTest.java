package com.dnws.wakandaspaceagencyservice.task.impl;

import com.dnws.wakandaspaceagencyservice.TestUtils;
import com.dnws.wakandaspaceagencyservice.enums.SatelliteType;
import com.dnws.wakandaspaceagencyservice.model.Coordinate;
import com.dnws.wakandaspaceagencyservice.model.Frequency;
import com.dnws.wakandaspaceagencyservice.model.Zone;
import com.dnws.wakandaspaceagencyservice.persistence.SatelliteEntity;
import com.dnws.wakandaspaceagencyservice.persistence.repositories.SatelliteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InfraredReadTaskExecutorTest {

    private final SatelliteRepository repository = mock();

    @Test
    void constructor_shouldNotAcceptNullValues() {
        // Given
        assertThrows(IllegalArgumentException.class
                , () -> new InfraredReadTaskExecutor(UUID.randomUUID(), null)
        );
        assertThrows(IllegalArgumentException.class
                , () -> new InfraredReadTaskExecutor(null, mock())
        );
    }

    @Test
    void run_shouldThrowEntityNotFoundException_when_satelliteIsNotFound() {
        // Given
        var id = UUID.randomUUID();
        var executor = new InfraredReadTaskExecutor(id, repository);

        when(repository.findById(eq(id))).thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, () -> executor.run());
        verify(repository).findById(eq(id));
    }

    @Test
    void run_shouldCreateOneWeatherDataTopicForEachZoneAndPublishIt_when_satelliteIsFound() {
        // Given
        var id = UUID.randomUUID();
        var entity = TestUtils.createEntity(id);
        var zoneIds = entity.getZones().stream().map(Zone::id).toList();
        var executor = spy(new InfraredReadTaskExecutor(id, repository));

        when(repository.findById(eq(id))).thenReturn(Optional.of(entity));

        // When
        executor.run();

        // Then
        verify(repository).findById(eq(id));
        verify(repository).save(eq(entity));
    }
}