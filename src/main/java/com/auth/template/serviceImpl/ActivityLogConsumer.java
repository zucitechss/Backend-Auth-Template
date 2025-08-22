package com.auth.template.serviceImpl;

import com.auth.template.entity.UserActivityLog;
import com.auth.template.repository.UserActivityLogRepository;
import com.auth.template.requestDTO.UserActivityEvent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

@Service
public class ActivityLogConsumer {

    @Autowired
    private BlockingQueue<UserActivityEvent> activityQueue;

    @Autowired
    private UserActivityLogRepository repository;

    @PostConstruct
    public void startConsumer() {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                try {
                    UserActivityEvent event = activityQueue.take();
                    UserActivityLog log = new UserActivityLog();
                    log.setUsername(event.getUsername());
                    log.setAction(event.getAction());
                    log.setDetails(event.getDetails());
                    log.setIpAddress(event.getIpAddress());
                    log.setTimestamp(event.getTimestamp());
                    log.setStatusCode(event.getStatusCode());
                    repository.save(log);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
}
