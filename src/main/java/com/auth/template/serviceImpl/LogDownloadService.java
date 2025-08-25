package com.auth.template.serviceImpl;

import com.auth.template.entity.UserActivityLog;
import com.auth.template.repository.UserActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class LogDownloadService {

    @Autowired
    private UserActivityLogRepository logRepository;

    public ResponseEntity<Resource> downloadLogs() throws IOException {
        List<UserActivityLog> logs = logRepository.findAll();

        File file = File.createTempFile("user_activity_log", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (UserActivityLog log : logs) {
                String line = String.format("%s | %s | %s | %s | IP: %s%n",
                        log.getTimestamp(), log.getUsername(), log.getAction(),
                        log.getDetails(), log.getIpAddress());
                writer.write(line);
            }
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user_activity_log.txt")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(file.length())
                .body(resource);
    }
}
