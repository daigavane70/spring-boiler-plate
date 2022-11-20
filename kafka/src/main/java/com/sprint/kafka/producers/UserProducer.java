package com.sprint.kafka.producers;

import com.sprint.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserProducer {
    @Autowired
    KafkaTemplate<String, User> kafkaTemplate;

    private static final String TOPIC = "test";

    public void sendMessage(User user) {
        kafkaTemplate.send(TOPIC, user);
    }
}
