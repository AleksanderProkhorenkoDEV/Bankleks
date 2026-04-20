package com.example.back.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.back.entities.auth.RefreshToken;
import com.example.back.entities.user.User;
import com.example.back.repositories.RefreshTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration-ms}")
    private Long refreshExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepo, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepo;
        this.userService = userService;
    }

    @Transactional
    public RefreshToken createRefreshToken(String email) {

        User user = userService.getUser(email);

        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken(
                user,
                UUID.randomUUID().toString(),
                Instant.now().plusMillis(refreshExpirationMs));

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean isExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    @Scheduled(cron = "0 0 * * * *")
    public void purgeExpiredTokens() {
        refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }
}
