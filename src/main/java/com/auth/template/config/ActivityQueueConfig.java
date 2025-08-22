package com.auth.template.config;

import com.auth.template.requestDTO.UserActivityEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class ActivityQueueConfig {
    @Bean
    public BlockingQueue<UserActivityEvent> activityQueue() {
        return new LinkedBlockingQueue<>();
    }
}
