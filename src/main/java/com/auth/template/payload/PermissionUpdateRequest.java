package com.auth.template.payload;

import com.auth.template.entity.Permission;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
@Builder
public class PermissionUpdateRequest {
    private String action;
    private String userName;
    private Set<Permission> permissions;
}
