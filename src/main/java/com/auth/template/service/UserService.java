package com.auth.template.service;

import com.auth.template.entity.User;
import com.auth.template.requestDTO.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<User> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir);
    String updateUser(UUID id, UserUpdateRequest userUpdateRequest);
    void deleteUser(UUID id);
}