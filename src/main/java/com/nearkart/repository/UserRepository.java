package com.nearkart.repository;

import com.nearkart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find user using email for Login
    Optional<User> findByEmail(String email);

    // Check whether email already exists during Register
    boolean existsByEmail(String email);

    // Check whether phone already exists during Register
    boolean existsByPhone(String phone);
}