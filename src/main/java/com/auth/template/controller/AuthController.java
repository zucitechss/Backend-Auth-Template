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
    @PostMapping(value = {"/register"})
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
    @PostMapping(value = {"/login"})
    public ResponseEntity<JWTAuthResponse> signIn(@RequestBody @Valid SignInDTO signInDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.login(signInDTO));
    }

    @Operation(summary = "Add role to user", description = "Adds or delete roles for a user")
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

    @Operation(summary = "Refresh JWT token", description = "Generates a new access token using a valid refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<JWTAuthResponse> refreshToken(@RequestParam String refreshToken) {
        JWTAuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Add permission to user", description = "Adds or delete permissions for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permission updated successfully"),
        @ApiResponse(responseCode = "404", description = "User or permission not found")
    })
    @PostMapping("/addPermission")
    public ResponseEntity<Response> addPermissionToUser(@RequestBody @Valid PermissionUpdateRequest permissionUpdateRequest) {
         String statusMsg = authService.addPermissionToUser(permissionUpdateRequest);
          return ResponseEntity
                 .status(HttpStatus.OK)
                 .body(Response.builder()
                            .statusMsg(statusMsg)
                            .statusCode(HttpStatus.OK.value())
                            .build());
    }

    @Operation(summary = "Get all permissions", description = "Fetches all permissions from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permissions fetched successfully"),
        @ApiResponse(responseCode = "404", description = "No permissions found")
    })
    @GetMapping("/getAllPermissions")
    public ResponseEntity<?> getAllPermissions() {
        try {
            var permissions = authService.getAllPermissions();
            if (permissions == null || permissions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No permissions found.");
            }
            return ResponseEntity.ok(permissions);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching permissions: " + ex.getMessage());
        }
    }

    @Operation(summary = "Get all roles", description = "Fetches all roles from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Roles fetched successfully"),
        @ApiResponse(responseCode = "404", description = "No roles found")
    })
    @GetMapping("/getAllRoles")
    public ResponseEntity<?> getAllRoles() {
        try {
            var roles = authService.getAllRoles();
            if (roles == null || roles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No roles found.");
            }
            return ResponseEntity.ok(roles);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching roles: " + ex.getMessage());
        }
    }
}
