package com.example.back.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.example.back.entities.auth.RefreshToken;
import com.example.back.entities.auth.Role;
import com.example.back.entities.user.User;
import com.example.back.repositories.RefreshTokenRepository;
import com.example.back.repositories.RoleRepository;
import com.example.back.repositories.UserRepository;
import com.example.back.services.JwtService;
import com.example.back.services.RefreshTokenService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final RoleRepository roleRepository;

    @Value("${app.cookie.secure:true}")
    private boolean secureCookie;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
            UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService,
            RefreshTokenRepository refreshTokenRepository, RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse response) {
        UserDetails user = authenticate(request);

        String role = user.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        String token = jwtService.generateToken(user.getUsername(), role);
        RefreshToken refreshToken = this.refreshTokenService.createRefreshToken(user.getUsername());

        createRefreshCookie(refreshToken.getToken(), response);

        return ResponseEntity.ok(new LoginResponseDTO(
                token,
                user.getUsername(),
                role));
    }

    private void createRefreshCookie(String refreshToken, HttpServletResponse response) {
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(secureCookie);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(refreshCookie);
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
    public ResponseEntity<GlobalResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new GlobalResponseDTO("El email ya esta en uso", HttpStatus.BAD_REQUEST.value()));
        }

        userRepository.save(createUser(request));

        return ResponseEntity.ok(new GlobalResponseDTO("Usuario creado correctamente", HttpStatus.OK.value()));
    }

    private User createUser(RegisterRequestDTO request) {
        Role role = roleRepository.findByName("CLIENT")
                .orElseThrow(EntityNotFoundException::new);

        return new User(request.getName(), request.getEmail(), passwordEncoder.encode(request.getPassword()), role);
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
