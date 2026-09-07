package com.nearkart.service;

import com.nearkart.dto.UserDTO;
import com.nearkart.entity.User;
import com.nearkart.exception.ResourceNotFoundException;
import com.nearkart.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;


    // =========================
    // CONSTRUCTOR
    // =========================

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // =========================
    // CREATE USER
    // =========================

    public User createUser(User user) {

        return userRepository.save(user);
    }


    // =========================
    // GET ALL USERS
    // =========================

    public List<UserDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // =========================
    // GET USER BY ID
    // =========================

    public UserDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );

        return convertToDTO(user);
    }


    // =========================
    // DELETE USER
    // =========================

    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );

        userRepository.delete(user);
    }


    // =========================
    // CONVERT USER TO DTO
    // =========================

    public UserDTO convertToDTO(User user) {

        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());

        return dto;
    }
}