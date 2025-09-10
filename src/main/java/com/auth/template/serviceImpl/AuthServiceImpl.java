package com.auth.template.serviceImpl;

import com.auth.template.constants.AppConstant;
import com.auth.template.entity.Permission;
import com.auth.template.entity.Role;
import com.auth.template.entity.User;
import com.auth.template.exception.AuthException;
import com.auth.template.mapper.UserMapper;
import com.auth.template.repository.PermissionRepository;
import com.auth.template.repository.RoleRepository;
import com.auth.template.repository.UserRepository;
import com.auth.template.requestDTO.*;
import com.auth.template.responseDTO.JWTAuthResponse;
import com.auth.template.security.JwtTokenProvider;
import com.auth.template.service.AuthService;
import com.auth.template.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
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
import java.util.List;
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

    @Autowired
    private AuditorAware<String> auditorAware;

    private static final int OTP_LENGTH = 6;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, EmailService emailService, PermissionRepository permissionRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public JWTAuthResponse login(SigninRequest signinRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getUserNameOrEmail(), signinRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNameOrEmail(userName, userName).orElseThrow(() -> new UsernameNotFoundException("User not found with username or email :" + userName));
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        return new JWTAuthResponse(accessToken, refreshToken, user.getId(), user.getUserName(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRoles(), true, user.isEmailVerified(), user.getPermissions());
    }

    @Override
    public String register(SignupRequest signupRequest) {
        if (userRepository.existsByUserName(signupRequest.getUsername())) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "UserName is already taken !..");
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Email is already taken !..");
        }
        if (!signupRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Invalid email format !..");
        }
        if (!signupRequest.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{5,}$")) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Password must be at least 5 characters long, contain upper and lower case letters, a digit, and a special character");
        }

        User user = UserMapper.mapToUser(signupRequest, new User());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        Role roles = roleRepository.findByName(AppConstant.ROLE_EMPLOYEE).orElseThrow(() -> new UsernameNotFoundException("Role not found with name: " + AppConstant.ROLE_EMPLOYEE));
        user.setRoles(Collections.singleton(roles));

        Permission defaultPermission = permissionRepository.findByName(AppConstant.PERMISSION_DEFAULT).orElseThrow(() -> new UsernameNotFoundException("Permission not found with name: " + AppConstant.PERMISSION_DEFAULT));
        user.setPermissions(Collections.singleton(defaultPermission));

        userRepository.save(user);
        emailService.sendSignupSuccessEmail(signupRequest.getEmail(), signupRequest.getUsername());
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
                user.getRoles().addAll(addedRole);
                userRepository.save(user);
                return "Role added successfully!";
            case "DELETE":
                user.getRoles().removeAll(addedRole);
                userRepository.save(user);
                return "Role deleted successfully!";
            default:
                throw new IllegalArgumentException("Invalid action: " + roleUpdateRequest.getAction());
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
            addedPermissions.add(permission);
        });

        switch (permissionUpdateRequest.getAction()) {
            case "ADD":
                user.getPermissions().addAll(addedPermissions);
                userRepository.save(user);
                return "Permission added successfully!";
            case "DELETE":
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
            throw new AuthException(HttpStatus.BAD_REQUEST, "OTP expired or not requested.");
        }
        if (!user.getResetOtp().equals(resetPasswordRequest.getOtp())) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Invalid OTP.");
        }
        if (resetPasswordRequest.getNewPassword() == null || resetPasswordRequest.getNewPassword().isEmpty()) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Please provide a new password.");
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

    @Override
    public List<Permission> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        if (permissions == null || permissions.isEmpty()) {
            throw new UsernameNotFoundException("No permissions found.");
        }
        return permissions;
    }

    @Override
    public List<Role> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        if (roles == null || roles.isEmpty()) {
            throw new UsernameNotFoundException("No roles found.");
        }
        return roles;
    }

    @Override
    public String addPermission(PermissionCreateRequest permissionCreateRequest) {
        if (permissionRepository.findByName(permissionCreateRequest.getName()).isPresent()) {
            return "Permission already exists";
        }
        Permission permission = new Permission();
        permission.setName(permissionCreateRequest.getName());
        permissionRepository.save(permission);
        return "Permission added successfully";
    }

    @Override
    public String deletePermission(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId).orElse(null);
        if (permission == null) {
            return "Permission not found";
        }
        permissionRepository.delete(permission);
        return "Permission deleted successfully";
    }

    @Override
    public String addRole(RoleCreateRequest roleCreateRequest) {
        if (roleRepository.findByName(roleCreateRequest.getName()).isPresent()) {
            return "Role already exists";
        }
        Role role = new Role();
        role.setName(roleCreateRequest.getName());
        roleRepository.save(role);
        return "Role added successfully";
    }

    @Override
    public String deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            return "Role not found";
        }
        roleRepository.delete(role);
        return "Role deleted successfully";
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
