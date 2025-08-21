package com.auth.template.serviceImpl;


import com.auth.template.auditor.Audit;
import com.auth.template.entity.User;
import com.auth.template.mapper.UserMapper;
import com.auth.template.repository.PermissionRepository;
import com.auth.template.repository.RoleRepository;
import com.auth.template.repository.UserRepository;
import com.auth.template.requestDTO.UserUpdateRequest;
import com.auth.template.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users == null || users.isEmpty()) {
            throw new UsernameNotFoundException("No users found.");
        }
        return users;
    }

    @Override
    public String updateUser(UUID id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        User updatedUser = UserMapper.mapToUser(userUpdateRequest,user);
        userRepository.save(updatedUser);

        return "User updated successfully";
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        if (user.getRoles() != null) {
            user.getRoles().clear();
        }
        if (user.getPermissions() != null) {
            user.getPermissions().clear();
        }
        userRepository.save(user);

        userRepository.delete(user);
    }
}