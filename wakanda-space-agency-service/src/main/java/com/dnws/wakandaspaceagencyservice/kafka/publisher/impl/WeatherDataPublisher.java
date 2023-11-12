package com.dnws.wakandaspaceagencyservice.kafka.publisher.impl;

import com.dnws.wakandaspaceagencyservice.kafka.EventPublisher;
import com.dnws.wakandaspaceagencyservice.kafka.model.WeatherDataTopic;
import com.dnws.wakandaspaceagencyservice.kafka.publisher.IPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WeatherDataPublisher implements IPublisher<WeatherDataTopic, UUID> {
    public static final String WEATHER_TOPIC = "wasa.satellite.weather.topic";

    private final EventPublisher<WeatherDataTopic> eventPublisher;

    private WeatherDataTopic data;
    private UUID messageKey;

    public WeatherDataPublisher(EventPublisher<WeatherDataTopic> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    @Override
    public EventPublisher<WeatherDataTopic> getEventPublisher() {
        return eventPublisher;
    }

    @Override
    public void publish(UUID messageKey, WeatherDataTopic data) {
        this.messageKey =  messageKey;
        this.data = data;
        this.publish();
    }

    @Override
    public WeatherDataTopic getData() {
        return this.data;
    }

    @Override
    public String getMessageKey() {
        return messageKey.toString();
    }

    @Override
    public String getTopicName() {
        return WEATHER_TOPIC;
    }
}
