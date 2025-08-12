package com.auth.template.serviceImpl;

import com.auth.template.entity.Permission;
import com.auth.template.entity.Role;
import com.auth.template.entity.User;
import com.auth.template.payload.UserDTO;
import com.auth.template.repository.PermissionRepository;
import com.auth.template.repository.RoleRepository;
import com.auth.template.repository.UserRepository;
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
    public User updateUser(UUID id, UserDTO updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        user.setUserName(updatedUser.getUsername());

        if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            Set<Role> validRoles = new HashSet<>();
            for (Role role : updatedUser.getRoles()) {
                Role foundRole = roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new UsernameNotFoundException("Role not found: " + role.getName()));
                validRoles.add(foundRole);
            }
            user.setRoles(validRoles);
        }

        if (updatedUser.getPermissions() != null && !updatedUser.getPermissions().isEmpty()) {
            Set<Permission> validPermissions = new HashSet<>();
            for (Permission permission : updatedUser.getPermissions()) {
                Permission foundPermission = permissionRepository.findByName(permission.getName())
                        .orElseThrow(() -> new UsernameNotFoundException("Permission not found: " + permission.getName()));
                validPermissions.add(foundPermission);
            }
            user.setPermissions(validPermissions);
        }

        return userRepository.save(user);
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