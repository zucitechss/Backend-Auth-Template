package com.auth.template.repository;

import com.auth.template.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUserNameOrEmail(String userName, String email);

    Optional<User> findByUserName(String userName);

    Boolean existsByUserName(String userName);

    Boolean existsByEmail(String email);
}
