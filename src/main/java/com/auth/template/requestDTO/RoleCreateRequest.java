package com.auth.template.requestDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleCreateRequest {
    @NotBlank(message = "Role name is required")
    private String name;
}

