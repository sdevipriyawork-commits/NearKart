package com.nearkart.service;

import com.nearkart.dto.AuthResponse;
import com.nearkart.dto.LoginRequest;
import com.nearkart.dto.RegisterRequest;
import com.nearkart.dto.UserDTO;
import com.nearkart.entity.User;
import com.nearkart.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    // =========================
    // CONSTRUCTOR
    // =========================

    public AuthService(
            UserRepository userRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }


    // =========================
    // REGISTER USER
    // =========================

    public UserDTO register(RegisterRequest request) {

        // CHECK EMAIL
        if (userRepository.existsByEmail(request.getEmail())) {

            throw new RuntimeException(
                    "Email already registered"
            );
        }


        // CHECK PHONE
        if (userRepository.existsByPhone(request.getPhone())) {

            throw new RuntimeException(
                    "Phone number already registered"
            );
        }


        // CREATE USER
        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());


        // =========================
        // ENCRYPT PASSWORD 🔐
        // =========================

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );


        user.setPhone(request.getPhone());


        // =========================
        // DEFAULT ROLE
        // =========================

        if (request.getRole() == null ||
                request.getRole().isEmpty()) {

            user.setRole("CUSTOMER");

        } else {

            user.setRole(request.getRole());
        }


        // CREATED TIME
        user.setCreatedAt(LocalDateTime.now());


        // SAVE USER
        User savedUser =
                userRepository.save(user);


        // RETURN SAFE DTO
        return convertToDTO(savedUser);
    }


    // =========================
    // LOGIN USER + JWT
    // =========================

    public AuthResponse login(LoginRequest request) {

        // FIND USER BY EMAIL
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid email or password"
                        )
                );


        // =========================
        // CHECK ENCRYPTED PASSWORD 🔐
        // =========================

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }


        // =========================
        // GENERATE JWT TOKEN
        // =========================

        String token = jwtService.generateToken(user);


        // =========================
        // RETURN TOKEN RESPONSE
        // =========================

        return new AuthResponse(
                token,
                "Login successful",
                user.getRole(),
                user.getId()
        );
    }


    // =========================
    // CONVERT USER TO DTO
    // =========================

    private UserDTO convertToDTO(User user) {

        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());

        return dto;
    }
}