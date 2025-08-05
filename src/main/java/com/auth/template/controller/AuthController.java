package com.auth.template.controller;

import com.auth.template.payload.*;
import com.auth.template.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/auth/")
@CrossOrigin("*")
@Tag(name = "Authentication Controller", description = "APIs for user authentication and management")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<Response> registerUser(@RequestBody @Valid SignUpDTO signUpDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.builder()
                        .statusMsg(authService.register(signUpDTO))
                        .statusCode(HttpStatus.CREATED.value())
                        .build());
    }

    @Operation(summary = "Login user", description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JWTAuthResponse> signIn(@RequestBody @Valid SignInDTO signInDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.login(signInDTO));
    }

    @Operation(summary = "Add role to user", description = "Adds or removes roles for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role updated successfully"),
        @ApiResponse(responseCode = "404", description = "User or role not found")
    })
    @PostMapping("/addRole")
    public ResponseEntity<Response> addRoleToUser(@RequestBody @Valid RoleUpdateRequest roleUpdateRequest) {
         String statusMsg = authService.addRoleToUser(roleUpdateRequest);
          return ResponseEntity
                 .status(HttpStatus.OK)
                 .body(Response.builder()
                            .statusMsg(statusMsg)
                            .statusCode(HttpStatus.OK.value())
                            .build());

    }

    @Operation(summary = "Reset user password", description = "Sends OTP to email and resets password after verification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset or OTP sent"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid OTP or password")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Response> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        String statusMsg = authService.resetPassword(resetPasswordRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.builder()
                        .statusMsg(statusMsg)
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
}
