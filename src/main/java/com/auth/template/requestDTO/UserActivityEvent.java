package com.auth.template.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityEvent {
    private String username;
    private String action;
    private int statusCode;
    private String details;
    private String ipAddress;
    private LocalDateTime timestamp;
}
