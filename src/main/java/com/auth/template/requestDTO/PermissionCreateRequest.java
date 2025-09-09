package com.auth.template.requestDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionCreateRequest {
    @NotBlank(message = "Permission name is required")
    private String name;
}

