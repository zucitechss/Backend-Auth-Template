package com.auth.template.service;

import com.auth.template.payload.JWTAuthResponse;
import com.auth.template.payload.ResetPasswordRequest;
import com.auth.template.payload.RoleUpdateRequest;
import com.auth.template.payload.SignInDTO;
import com.auth.template.payload.SignUpDTO;
import jakarta.validation.Valid;

public interface AuthService {
    JWTAuthResponse login(SignInDTO signInDTO);

    String register(SignUpDTO signUpDTO);

    String addRoleToUser(@Valid RoleUpdateRequest roleUpdateRequest);

    String resetPassword(ResetPasswordRequest resetPasswordRequest);

    JWTAuthResponse refreshToken(String refreshToken);
//
//    void logout(String refreshToken);
//
//    void verifyEmail(String token);
//
//    void resendVerificationEmail(String email);
}
