package com.auth.template.payload;

import com.auth.template.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class JWTAuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private UUID id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private Set<Role> roles;
    private boolean isActive;
    private boolean isVerified;

    public JWTAuthResponse(String accessToken, String refreshToken, String tokenType, UUID id, String userName, String email, String firstName, String lastName, Set<Role> roles, boolean isActive, boolean isVerified) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
        this.isActive = isActive;
        this.isVerified = isVerified;
    }

    public JWTAuthResponse() {
    }
}
