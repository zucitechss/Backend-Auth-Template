package com.auth.template;

import com.auth.template.constants.AppConstant;
import com.auth.template.entity.Permission;
import com.auth.template.entity.Role;
import com.auth.template.entity.User;
import com.auth.template.repository.PermissionRepository;
import com.auth.template.repository.RoleRepository;
import com.auth.template.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@SpringBootApplication
public class AuthTemplateApplication implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public AuthTemplateApplication(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthTemplateApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Check and create 'role_create' permission
        Optional<Permission> roleCreatePermissionOpt = permissionRepository.findByName(AppConstant.PERMISSION_ROLE_CREATE);
        Permission roleCreatePermission;
        if (roleCreatePermissionOpt.isEmpty()) {
            roleCreatePermission = new Permission();
            roleCreatePermission.setName(AppConstant.PERMISSION_ROLE_CREATE);
            roleCreatePermission = permissionRepository.save(roleCreatePermission);
        } else {
            roleCreatePermission = roleCreatePermissionOpt.get();
        }

        // Check and create 'ROLE_ADMIN' role
        Optional<Role> adminRoleOpt = roleRepository.findByName(AppConstant.ROLE_ADMIN);
        Role adminRole;
        if (adminRoleOpt.isEmpty()) {
            adminRole = new Role();
            adminRole.setName(AppConstant.ROLE_ADMIN);
            adminRole = roleRepository.save(adminRole);
        } else {
            adminRole = adminRoleOpt.get();
        }

        // Check and create admin user
        String adminUsername = System.getenv().getOrDefault("ADMIN_USERNAME", AppConstant.ADMIN_USERNAME);
        String adminEmail = System.getenv().getOrDefault("ADMIN_EMAIL", AppConstant.ADMIN_EMAIL);
        String adminPassword = System.getenv().getOrDefault("ADMIN_PASSWORD", AppConstant.ADMIN_PASSWORD);
        Optional<User> existingAdmin = userRepository.findByUserNameOrEmail(adminUsername,adminEmail);
        if (existingAdmin.isEmpty()) {
            User adminUser = new User();
            adminUser.setUserName(adminUsername);
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setFirstName(AppConstant.ADMIN_USER_FIRSTNAME);
            adminUser.setLastName(AppConstant.ADMIN_USER_LASTNAME);
            adminUser.setEmailVerified(true);
            adminUser.setRoles(Set.of(adminRole));
            adminUser.setPermissions(Set.of(roleCreatePermission));
            userRepository.save(adminUser);
        }
    }
}
