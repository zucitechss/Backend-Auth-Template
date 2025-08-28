package com.auth.template.mapper;

import com.auth.template.entity.User;
import com.auth.template.requestDTO.SignupRequest;
import com.auth.template.requestDTO.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public static User mapToUser(UserUpdateRequest userUpdateRequest, User user) {
       user.setUserName(userUpdateRequest.getUsername());
       user.setPassword(userUpdateRequest.getPassword());
       user.setFirstName(userUpdateRequest.getFirstName());
       user.setLastName(userUpdateRequest.getLastName());

       return user;
    }

    public static User mapToUser(SignupRequest signupRequest, User user) {
        user.setUserName(signupRequest.getUsername());
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setEmail(signupRequest.getEmail());
        return user;
    }
}
