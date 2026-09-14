package com.nearkart.controller;

import com.nearkart.dto.UserDTO;
import com.nearkart.entity.User;
import com.nearkart.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================
    // CREATE USER
    // =========================

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // =========================
    // GET ALL USERS
    // =========================

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // =========================
    // GET USER BY ID
    // =========================

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // =========================
    // UPDATE USER PROFILE
    // =========================

    @PutMapping("/{id}")
    public UserDTO updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails,
            Authentication authentication) {

        // Get currently logged-in user from JWT
        User loggedInUser =
                (User) authentication.getPrincipal();

        // ADMIN can update any user
        if (!"ADMIN".equals(loggedInUser.getRole())) {

            // Other users can update only their own profile
            if (!loggedInUser.getId().equals(id)) {

                throw new RuntimeException(
                        "You can update only your own profile"
                );
            }
        }

        return userService.updateUser(id, userDetails);
    }

    // =========================
    // DELETE USER
    // =========================

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}