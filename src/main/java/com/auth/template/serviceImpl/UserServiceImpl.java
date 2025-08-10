package com.auth.template.serviceImpl;

import com.auth.template.entity.User;
import com.auth.template.repository.UserRepository;
import com.auth.template.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users == null || users.isEmpty()) {
            throw new UsernameNotFoundException("No users found.");
        }
        return users;
    }
}