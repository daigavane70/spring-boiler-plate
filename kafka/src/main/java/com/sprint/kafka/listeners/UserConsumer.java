package com.sprint.kafka.listeners;

import com.sprint.repository.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserConsumer {
    @KafkaListener(topics = "test", groupId = "group_id")
    public void consume(User user) {
        log.info("ConsumedMessage: {}", user);
    }
}
