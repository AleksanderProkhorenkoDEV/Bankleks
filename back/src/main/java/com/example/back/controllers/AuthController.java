package com.example.back.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.auth.LoginRequestDTO;
import com.example.back.dto.auth.LoginResponseDTO;
import com.example.back.dto.auth.RegisterRequestDTO;
import com.example.back.entities.auth.RefreshToken;
import com.example.back.entities.user.User;
import com.example.back.repositories.RefreshTokenRepository;
import com.example.back.repositories.UserRepository;
import com.example.back.services.JwtService;
import com.example.back.services.RefreshTokenService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
            UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService,
            RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        UserDetails user = authenticate(request);

        String token = jwtService.generateToken(user.getUsername());
        RefreshToken refreshToken = this.refreshTokenService.createRefreshToken(user.getUsername());

        return ResponseEntity.ok(new LoginResponseDTO(
                token,
                refreshToken.getToken(),
                user.getUsername()));
    }

    private UserDetails authenticate(LoginRequestDTO request) {

        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        if (!(auth.getPrincipal() instanceof UserDetails user)) {
            throw new IllegalStateException("Unexpected type");
        }
        return user;

    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("El email ya está registrado");
        }

        userRepository.save(createUser(request));

        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    private User createUser(RegisterRequestDTO request) {
        return new User(request.getName(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String requestToken = body.get("refreshToken");

        return refreshTokenRepository.findByToken(requestToken)
                .map(token -> {
                    if (refreshTokenService.isExpired(token)) {
                        refreshTokenRepository.delete(token);
                        return ResponseEntity.badRequest().body("Refresh token expirado, vuelve a hacer login.");
                    }
                    String newAccessToken = jwtService.generateToken(token.getUser().getEmail());
                    return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
                })
                .orElse(ResponseEntity.badRequest().body("Refresh token inválido."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        String requestToken = body.get("refreshToken");

        return refreshTokenRepository.findByToken(requestToken)
                .map(token -> {
                    refreshTokenRepository.delete(token);
                    return ResponseEntity.ok("Logout correcto.");
                })
                .orElse(ResponseEntity.badRequest().body("Refresh token inválido."));
    }
}
