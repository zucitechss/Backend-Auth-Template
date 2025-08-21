package com.auth.template.requestDTO;

import com.auth.template.entity.Permission;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
@Builder
public class PermissionUpdateRequest {
    @NotEmpty(message = "This field is required it can not be empty")
    private String action;
    @NotEmpty(message = "This field is required it can not be empty")
    private String userName;
    private Set<Permission> permissions;
}
