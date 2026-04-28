package com.example.back.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.back.dto.auth.RegisterRequestDTO;
import com.example.back.entities.auth.Role;
import com.example.back.repositories.RoleRepository;
import com.example.back.repositories.UserRepository;
import com.example.back.entities.user.User;


import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AccountService accountService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;

    }

    public void registerUser(RegisterRequestDTO request){
        User user = createUser(request);
        this.userRepository.save(user);
        this.createAccount(user);
    }

    private User createUser (RegisterRequestDTO request) {
        Role role = roleRepository.findByName("CLIENT")
                .orElseThrow(EntityNotFoundException::new);

        return new User(request.getName(), request.getEmail(), passwordEncoder.encode(request.getPassword()), role);
    }

    private void createAccount (User user){
        this.accountService.createAccount(user);
    }

}
