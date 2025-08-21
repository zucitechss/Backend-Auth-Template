package com.auth.template.controller;

import com.auth.template.entity.User;
import com.auth.template.payload.UserDTO;
import com.auth.template.requestDTO.UserUpdateRequest;
import com.auth.template.responseDTO.GenericResponse;
import com.auth.template.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            if (users == null || users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No users found.");
            }
            return ResponseEntity.ok(users);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching users: " + ex.getMessage());
        }
    }

    @PutMapping("/api/users/{id}")
    public ResponseEntity<GenericResponse> updateUser(@PathVariable UUID id, @RequestBody UserUpdateRequest updatedUser) {
        try {
            String message = userService.updateUser(id, updatedUser);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(GenericResponse.builder().statusCode(HttpStatus.OK.value()).statusMsg(message).build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.builder().statusCode(HttpStatus.NOT_FOUND.value()).statusMsg(ex.getMessage()).build());
        }
    }

    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found or could not be deleted: " + ex.getMessage());
        }
    }

}

