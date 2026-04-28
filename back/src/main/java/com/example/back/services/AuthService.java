package com.example.back.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.back.dto.auth.LoginRequestDTO;
import com.example.back.dto.auth.LoginResponseDTO;
import com.example.back.dto.auth.RegisterRequestDTO;
import com.example.back.entities.auth.RefreshToken;
import com.example.back.entities.auth.Role;
import com.example.back.repositories.RoleRepository;
import com.example.back.repositories.UserRepository;
import com.example.back.entities.user.User;
import jakarta.servlet.http.Cookie;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AccountService accountService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    public void registerUser(RegisterRequestDTO request) {
        User user = createUser(request);
        this.userRepository.save(user);
        this.createAccount(user);
    }

    private User createUser(RegisterRequestDTO request) {
        Role role = roleRepository.findByName("CLIENT")
                .orElseThrow(EntityNotFoundException::new);

        return new User(request.getName(), request.getEmail(), passwordEncoder.encode(request.getPassword()), role);
    }

    private void createAccount(User user) {
        this.accountService.createAccount(user);
    }

    private User authenticate(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // si la autenticación pasa, buscamos nuestra entidad
        return userRepository.findByEmail(request.getEmail())
                .orElseThrow(EntityNotFoundException::new);
    }

    public LoginResponseDTO login(LoginRequestDTO request, HttpServletResponse response) {
        User user = authenticate(request);

        String role = user.getRole().getName();

        String token = jwtService.generateToken(user.getEmail(), role);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        createRefreshCookie(refreshToken.getToken(), response);

        return new LoginResponseDTO(token, user.getEmail(), role, user.getId());
    }

    private void createRefreshCookie(String refreshToken, HttpServletResponse response) {
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/auth");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshCookie);
    }

}
