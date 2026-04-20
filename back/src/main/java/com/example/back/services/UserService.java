package com.example.back.services;

import org.springframework.stereotype.Service;

import com.example.back.entities.user.User;
import com.example.back.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
