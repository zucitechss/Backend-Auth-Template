package com.auth.template.service;

import com.auth.template.entity.User;
import com.auth.template.payload.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<User> getAllUsers();
    User updateUser(UUID id, UserDTO updatedUser);
    void deleteUser(UUID id);
}