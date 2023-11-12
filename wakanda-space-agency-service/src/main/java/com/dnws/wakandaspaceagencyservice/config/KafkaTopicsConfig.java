package com.dnws.wakandaspaceagencyservice.config;

import com.dnws.wakandaspaceagencyservice.kafka.publisher.impl.InfraredDataPublisher;
import com.dnws.wakandaspaceagencyservice.kafka.publisher.impl.WeatherDataPublisher;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {
    @Value("${spring.application.kafka.config.partitions}")
    public int partitions;

    @Value("${spring.application.kafka.config.replicas}")
    public int replicas;

    @Bean
    public NewTopic weather() {
        return TopicBuilder
                .name(WeatherDataPublisher.WEATHER_TOPIC)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic infrared() {
        return TopicBuilder
                .name(InfraredDataPublisher.INFRARED_TOPIC)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
