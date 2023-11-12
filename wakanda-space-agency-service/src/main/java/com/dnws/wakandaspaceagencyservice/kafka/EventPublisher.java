package com.dnws.wakandaspaceagencyservice.kafka;

import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class EventPublisher<T> {

    private final Logger logger = LoggerFactory.getLogger(EventPublisher.class);
    private final KafkaTemplate<String, T> kafkaTemplate;

    public EventPublisher(KafkaTemplate<String, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String topic, String key, T event) {
        CompletableFuture<SendResult<String, T>> completableFuture = kafkaTemplate.send(topic, key, event);
        completableFuture.whenComplete((result, ex)->{
            if(ex == null) {
                val producerRecord = result.getProducerRecord();
                logger.info("Successfully published message. key="
                        + producerRecord.key()
                        + " into "
                        + producerRecord.topic()
                        + " value "
                        + producerRecord.value()
                );
            } else {
                logger.error("Could not publish message with key=" + key, ex);
            }
        });
    }
}