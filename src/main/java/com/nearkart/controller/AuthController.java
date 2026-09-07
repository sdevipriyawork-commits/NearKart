package com.nearkart.controller;

import com.nearkart.dto.AuthResponse;
import com.nearkart.dto.LoginRequest;
import com.nearkart.dto.RegisterRequest;
import com.nearkart.dto.UserDTO;
import com.nearkart.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;


    // =========================
    // CONSTRUCTOR
    // =========================

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    // =========================
    // REGISTER
    // =========================

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(
            @RequestBody RegisterRequest request) {

        UserDTO user = authService.register(request);

        return ResponseEntity.ok(user);
    }


    // =========================
    // LOGIN + JWT TOKEN
    // =========================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        AuthResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }
}