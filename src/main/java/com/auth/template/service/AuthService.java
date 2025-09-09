package com.auth.template.service;

import com.auth.template.responseDTO.JWTAuthResponse;
import com.auth.template.requestDTO.PermissionCreateRequest;
import com.auth.template.requestDTO.PermissionUpdateRequest;
import com.auth.template.requestDTO.ResetPasswordRequest;
import com.auth.template.requestDTO.RoleUpdateRequest;
import com.auth.template.requestDTO.SigninRequest;
import com.auth.template.requestDTO.SignupRequest;

import java.util.List;

public interface AuthService {
    JWTAuthResponse login(SigninRequest signinRequest);

    String register(SignupRequest signupRequest);

    String addRoleToUser(RoleUpdateRequest roleUpdateRequest);

    String resetPassword(ResetPasswordRequest resetPasswordRequest);

    JWTAuthResponse refreshToken(String refreshToken);

    String addPermissionToUser(PermissionUpdateRequest permissionUpdateRequest);

    String addPermission(PermissionCreateRequest permissionCreateRequest);

    String deletePermission(Long permissionId);

    List<?> getAllPermissions();

    List<?> getAllRoles();
}
