package com.auth.template.service;

import com.auth.template.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<User> getAllUsers();
    User updateUser(UUID id, User updatedUser);
    void deleteUser(UUID id);
}