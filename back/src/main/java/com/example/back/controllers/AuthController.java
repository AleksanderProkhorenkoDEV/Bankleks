package com.example.back.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.GlobalResponseDTO;
import com.example.back.dto.auth.LoginRequestDTO;
import com.example.back.dto.auth.LoginResponseDTO;
import com.example.back.dto.auth.RefreshResponseDTO;
import com.example.back.dto.auth.RegisterRequestDTO;
import com.example.back.repositories.RefreshTokenRepository;
import com.example.back.repositories.UserRepository;
import com.example.back.services.AuthService;
import com.example.back.services.JwtService;
import com.example.back.services.RefreshTokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    @Value("${app.cookie.secure:true}")
    private boolean secureCookie;

    public AuthController(JwtService jwtService,
            UserRepository userRepository, RefreshTokenService refreshTokenService,
            RefreshTokenRepository refreshTokenRepository, AuthService authService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PostMapping("/register")
    public ResponseEntity<GlobalResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new GlobalResponseDTO("El email ya esta en uso", HttpStatus.BAD_REQUEST.value()));
        }

        this.authService.registerUser(request);

        return ResponseEntity.ok(new GlobalResponseDTO("Usuario creado correctamente", HttpStatus.OK.value()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponseDTO> refresh(@CookieValue("refreshToken") String requestToken) {
        return refreshTokenRepository.findByToken(requestToken)
                .map(token -> {
                    if (refreshTokenService.isExpired(token)) {
                        refreshTokenRepository.delete(token);
                        return ResponseEntity.badRequest()
                                .body(new RefreshResponseDTO("Refresh token expirado, vuelve a hacer login."));
                    }
                    String newAccessToken = jwtService.generateToken(token.getUser().getEmail(),
                            token.getUser().getRole().getName());
                    return ResponseEntity.ok(new RefreshResponseDTO("Token actualizado", newAccessToken));
                })
                .orElse(ResponseEntity.badRequest().body(new RefreshResponseDTO("Refresh token inválido.")));
    }

    @PostMapping("/logout")
    public ResponseEntity<GlobalResponseDTO> logout(@CookieValue("refreshToken") String requestToken,
            HttpServletResponse response) {

        refreshTokenRepository.findByToken(requestToken).ifPresent(refreshTokenRepository::delete);

        cleanCookie(response);

        return ResponseEntity.ok(new GlobalResponseDTO("Sesión cerrada", HttpStatus.OK.value()));
    }

    private void cleanCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // igual que al crearla
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(0); // la elimina
        response.addCookie(cookie);
    }
}
