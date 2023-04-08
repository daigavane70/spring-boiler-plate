package com.sprint.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {

    public static double count = 1;

    // @Scheduled(fixedDelay = 2000)
    // public void scheduleFixedDelayTask() {
    // log.info("[schedularCount] current count: {}", count);
    // count = count + Math.random() * 10;
    // }
}
