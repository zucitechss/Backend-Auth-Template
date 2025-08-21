package com.auth.template.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GenericResponse {
    private int statusCode;
    private String statusMsg;
}
