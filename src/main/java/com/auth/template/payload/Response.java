package com.auth.template.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Response {
    private int statusCode;
    private String statusMsg;
}
