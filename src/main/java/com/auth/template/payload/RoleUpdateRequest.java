package com.auth.template.payload;

import com.auth.template.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
@Builder
public class RoleUpdateRequest {
    private String action;
    private String userName;
    private Set<Role> roles;
}
