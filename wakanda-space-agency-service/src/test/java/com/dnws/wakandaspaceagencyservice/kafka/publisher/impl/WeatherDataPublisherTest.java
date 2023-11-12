package com.dnws.wakandaspaceagencyservice.kafka.publisher.impl;

import com.dnws.wakandaspaceagencyservice.TestUtils;
import com.dnws.wakandaspaceagencyservice.enums.WindRose;
import com.dnws.wakandaspaceagencyservice.kafka.EventPublisher;
import com.dnws.wakandaspaceagencyservice.kafka.model.WeatherDataTopic;
import com.dnws.wakandaspaceagencyservice.model.WeatherData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WeatherDataPublisherTest {

    private final EventPublisher<WeatherDataTopic> eventPublisher = mock();

    private WeatherDataPublisher publisher = spy(new WeatherDataPublisher(eventPublisher));


    @Test
    void constructor_shouldThrowException_when_eventPublisherIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new WeatherDataPublisher(null));
    }

    @Test
    void publishWithNoParams_shouldThrowException_when_messageKeyOrTopicNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> publisher.publish());
    }

    @Test
    void publishWithParams_shouldSetMessageKeyAndData() {
        Mockito.reset(publisher);
        // Given
        var id = UUID.randomUUID();
        var data = new WeatherDataTopic(
                UUID.randomUUID(),
                new WeatherData(
                        Math.random() * 10,
                        Math.random() * 10,
                        WindRose.values()[(new Random()).nextInt(WindRose.values().length)],
                        TestUtils.createZone(),
                        Instant.now().getEpochSecond(),
                        id
                )
        );

        // When
        publisher.publish(id, data);

        // Then
        assertEquals(id.toString(), publisher.getMessageKey());
        assertEquals(data, publisher.getData());
        verify(publisher).publish();
        verify(eventPublisher).publish(
                eq(publisher.getTopicName()),
                eq(id.toString()),
                eq(data)
        );

    }
}