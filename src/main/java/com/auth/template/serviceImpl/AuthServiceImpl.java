package com.auth.template.serviceImpl;

import com.auth.template.entity.Permission;
import com.auth.template.entity.Role;
import com.auth.template.entity.User;
import com.auth.template.payload.*;
import com.auth.template.repository.PermissionRepository;
import com.auth.template.repository.RoleRepository;
import com.auth.template.repository.UserRepository;
import com.auth.template.security.JwtTokenProvider;
import com.auth.template.service.AuthService;
import com.auth.template.utils.EmailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final PermissionRepository permissionRepository;

    private static final int OTP_LENGTH = 6;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, EmailService emailService,PermissionRepository permissionRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public JWTAuthResponse login(SignInDTO signInDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInDTO.getUserNameOrEmail(), signInDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNameOrEmail(userName, userName).orElseThrow(() -> new UsernameNotFoundException("User not found with username or email :" + userName));
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        return new JWTAuthResponse(accessToken,refreshToken,user.getId(), user.getUserName(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRoles(), true, user.isEmailVerified(), user.getPermissions());
    }

    @Override
    public String register(SignUpDTO signUpDTO) {
        if (userRepository.existsByUserName(signUpDTO.getUsername())) {
            return "UserName is already taken !..";
        }
        if (userRepository.existsByEmail(signUpDTO.getEmail())) {
            return "Email is already taken !..";
        }
        if (!signUpDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "Invalid email format!";
        }
        if (!signUpDTO.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{5,}$")) {
            return "Password must be at least 5 characters long, contain upper and lower case letters, a digit, and a special character";
        }

        User user = new User();
        user.setUserName(signUpDTO.getUsername());
        user.setFirstName(signUpDTO.getFirstName());
        user.setLastName(signUpDTO.getLastName());
        user.setEmail(signUpDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));

        Role roles = roleRepository.findByName("ROLE_EMPLOYEE").orElseThrow(() -> new UsernameNotFoundException("Role not found with name: ROLE_EMPLOYEE"));
        user.setRoles(Collections.singleton(roles));

        Permission defaultPermission = permissionRepository.findByName("default_permission").orElseThrow(() -> new UsernameNotFoundException("Permission not found with name: default_permission"));
        user.setPermissions(Collections.singleton(defaultPermission));

        userRepository.save(user);
        emailService.sendSignupSuccessEmail(signUpDTO.getEmail(), signUpDTO.getUsername());
        return "User registered successfully!";
    }

    @Override
    public String addRoleToUser(RoleUpdateRequest roleUpdateRequest) {
        Set<Role> addedRole = new HashSet<>();
        User user = userRepository.findByUserName(roleUpdateRequest.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userName: " + roleUpdateRequest.getUserName()));
        roleUpdateRequest.getRoles().forEach(roleName -> {
            Role role = roleRepository.findByName(roleName.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Role not found with name: " + roleName));
            roleRepository.save(role);
            addedRole.add(role);
        });

        switch (roleUpdateRequest.getAction()) {
            case "ADD":
                user.setRoles(addedRole);
                userRepository.save(user);
                return "Role added successfully!";
            case "REMOVE":
                addedRole.stream()
                        .filter(r -> !user.getRoles().remove(r))
                        .collect(Collectors.toSet());
                userRepository.save(user);
                return "Role removed successfully!";
            default:
                throw new IllegalArgumentException("Invalid action type: " + roleUpdateRequest.getAction());
        }

    }

    @Override
    public String addPermissionToUser(PermissionUpdateRequest permissionUpdateRequest) {
        Set<Permission> addedPermissions = new HashSet<>();
        User user = userRepository.findByUserName(permissionUpdateRequest.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userName: " + permissionUpdateRequest.getUserName()));
        permissionUpdateRequest.getPermissions().forEach(permissionName -> {
            Permission permission = permissionRepository.findByName(permissionName.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Permission not found with name: " + permissionName));
            permissionRepository.save(permission);
            addedPermissions.add(permission);
        });

        switch (permissionUpdateRequest.getAction()) {
            case "ADD":
                user.setPermissions(addedPermissions);
                userRepository.save(user);
                return "Permission added successfully!";
            case "REMOVE":
                addedPermissions.stream()
                        .filter(p -> !user.getPermissions().remove(p))
                        .collect(Collectors.toSet());
                userRepository.save(user);
                return "Permission removed successfully!";
            default:
                throw new IllegalArgumentException("Invalid action type: " + permissionUpdateRequest.getAction());
        }
    }

    @Override
    public String resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + resetPasswordRequest.getEmail()));

        if (resetPasswordRequest.getOtp() == null || resetPasswordRequest.getOtp().isEmpty()) {
            String otp = generateOtp();
            user.setResetOtp(otp);
            user.setResetOtpExpiry(LocalDateTime.now().plusMinutes(10));
            userRepository.save(user);
            emailService.sendOtpForPasswordReset(user.getEmail(), otp);
            return "OTP sent to your email address.";
        }

        if (user.getResetOtp() == null || user.getResetOtpExpiry() == null ||
                LocalDateTime.now().isAfter(user.getResetOtpExpiry())) {
            return "OTP expired or not requested.";
        }
        if (!user.getResetOtp().equals(resetPasswordRequest.getOtp())) {
            return "Invalid OTP.";
        }
        if (resetPasswordRequest.getNewPassword() == null || resetPasswordRequest.getNewPassword().isEmpty()) {
            return "Please provide a new password.";
        }
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        user.setResetOtp(null);
        user.setResetOtpExpiry(null);
        userRepository.save(user);
        return "Password reset successfully!";
    }

    @Override
    public JWTAuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
        String userNameOrEmail = jwtTokenProvider.getUserNameFromJWT(refreshToken);
        User user = userRepository.findByUserNameOrEmail(userNameOrEmail, userNameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + userNameOrEmail));
        String newAccessToken = jwtTokenProvider.generateTokenFromUser(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
        return new JWTAuthResponse(newAccessToken, newRefreshToken, user.getId(), user.getUserName(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRoles(), true, user.isEmailVerified(), user.getPermissions());
    }


    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
