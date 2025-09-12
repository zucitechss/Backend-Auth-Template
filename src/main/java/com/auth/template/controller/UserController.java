package com.auth.template.controller;

import com.auth.template.constants.AppConstant;
import com.auth.template.entity.User;
import com.auth.template.requestDTO.UserUpdateRequest;
import com.auth.template.responseDTO.GenericResponse;
import com.auth.template.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "User Controller", description = "APIs for user management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users", description = "Fetches a paginated list of all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
        @ApiResponse(responseCode = "404", description = "No users found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(name = "pageNo", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        try {
            List<User> users = userService.getAllUsers(pageNo, pageSize, sortBy, sortDir);
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

    @Operation(summary = "Update user", description = "Updates user details by user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
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

    @Operation(summary = "Delete user", description = "Deletes a user by user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
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
