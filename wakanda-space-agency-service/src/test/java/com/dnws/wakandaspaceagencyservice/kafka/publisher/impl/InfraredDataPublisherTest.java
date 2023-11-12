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
class InfraredDataPublisherTest {

    private final EventPublisher<String> eventPublisher = mock();

    private InfraredDataPublisher publisher = spy(new InfraredDataPublisher(eventPublisher));


    @Test
    void constructor_shouldThrowException_when_eventPublisherIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new InfraredDataPublisher(null));
    }

    @Test
    void publishWithNoParams_shouldThrowException_when_messageKeyOrTopicNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> publisher.publish());
    }

    @Test
    void publishWithParams_shouldSetMessageKeyAndData() {
        Mockito.reset(publisher);
        // Given
        var message = UUID.randomUUID().toString();
        var key = new String(message);

        // When
        publisher.publish(key, message);

        // Then
        assertEquals(key, publisher.getMessageKey());
        assertEquals(message, publisher.getData());
        verify(publisher).publish();
        verify(eventPublisher).publish(
                eq(publisher.getTopicName()),
                eq(key),
                eq(message)
        );

    }
}