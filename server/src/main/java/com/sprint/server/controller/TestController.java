package com.sprint.server.controller;

import com.sprint.kafka.producers.UserProducer;
import com.sprint.common.response.HttpApiResponse;
import com.sprint.repository.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @Autowired
    UserProducer userProducer;

    @GetMapping
    HttpApiResponse test() {
        return new HttpApiResponse("Test endpoint triggered");
    }

    @PostMapping
    HttpApiResponse testKafka() {
        User newUser = User.builder().id(((long) Math.random())).name("New User").build();
        userProducer.sendMessage(newUser);
        return new HttpApiResponse(newUser);
    }

}
