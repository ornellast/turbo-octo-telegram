package com.dnws.wakandaspaceagencyservice.kafka.publisher.impl;

import com.dnws.wakandaspaceagencyservice.kafka.EventPublisher;
import com.dnws.wakandaspaceagencyservice.kafka.publisher.IPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class InfraredDataPublisher implements IPublisher<String, String> {
    public static final String INFRARED_TOPIC = "wasa.satellite.infrared.topic";

    private final EventPublisher<String> eventPublisher;

    private String data;
    private String messageKey;

    public InfraredDataPublisher(EventPublisher<String> eventPublisher) {
        Assert.notNull(eventPublisher, "eventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
    }


    @Override
    public EventPublisher<String> getEventPublisher() {
        return eventPublisher;
    }

    @Override
    public void publish(String messageKey, String data) {
        this.messageKey =  messageKey;
        this.data = data;
        this.publish();
    }

    @Override
    public String getData() {
        return this.data;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }

    @Override
    public String getTopicName() {
        return INFRARED_TOPIC;
    }
}
