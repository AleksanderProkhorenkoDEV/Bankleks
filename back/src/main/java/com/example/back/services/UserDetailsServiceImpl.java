package com.example.back.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.back.entities.user.User;
import com.example.back.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository reposuserRepository;

    public UserDetailsServiceImpl(UserRepository repo) {
        this.reposuserRepository = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = reposuserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail()) // email como “username”
                .password(user.getPassword()) // password encriptada
                .roles("USER") // por ahora asignamos ROLE_USER
                .build();

    }
}
