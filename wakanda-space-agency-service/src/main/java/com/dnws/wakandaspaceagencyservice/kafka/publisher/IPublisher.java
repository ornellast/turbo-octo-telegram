package com.dnws.wakandaspaceagencyservice.kafka.publisher;

import com.dnws.wakandaspaceagencyservice.kafka.EventPublisher;
import com.dnws.wakandaspaceagencyservice.kafka.model.WeatherDataTopic;
import org.springframework.util.Assert;

public interface IPublisher<T, ID> {

    EventPublisher<T>  getEventPublisher();
    
    void publish(ID messageKey, T data);

    T getData();

    String getMessageKey();

    String getTopicName();

    default void publish() {
        Assert.notNull(getEventPublisher(), "EventPublisher cannot be null");
        Assert.notNull(getMessageKey(), "messageKey cannot be null");
        Assert.notNull(getTopicName(), "topicName cannot be null");
        getEventPublisher().publish(
                getTopicName(),
                getMessageKey(),
                getData()
        );
    }
}
