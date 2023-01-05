package com.sprint.kafka.producers;

import com.sprint.repository.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserProducer {
    @Autowired
    KafkaTemplate<String, User> kafkaTemplate;

    private static final String TOPIC = "test";

    public void sendMessage(User user) {
        log.info("[UserProducer : sendMessage] produced message: {}", user);
        kafkaTemplate.send(TOPIC, user);
    }
}
