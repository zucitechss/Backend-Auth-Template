package com.auth.template.service;

import com.auth.template.entity.User;
import com.auth.template.payload.UserDTO;
import com.auth.template.requestDTO.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<User> getAllUsers();
    String updateUser(UUID id, UserUpdateRequest userUpdateRequest);
    void deleteUser(UUID id);
}