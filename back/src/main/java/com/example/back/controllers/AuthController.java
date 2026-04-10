package com.example.back.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.auth.LoginRequestDTO;
import com.example.back.dto.auth.LoginResponseDTO;
import com.example.back.services.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        UserDetails user = authenticate(request);
        String token = jwtService.generateToken(user.getUsername());
        return ResponseEntity.ok(new LoginResponseDTO(token, user.getUsername()));
    }

    private UserDetails authenticate(LoginRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        if(!(auth.getPrincipal() instanceof UserDetails user)) throw new IllegalStateException("Unexpected type");  
        return user;
    }
}
