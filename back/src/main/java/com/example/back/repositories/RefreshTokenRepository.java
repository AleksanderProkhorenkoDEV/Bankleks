package com.example.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back.entities.auth.RefreshToken;
import com.example.back.entities.user.User;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    void deleteByExpiryDateBefore(Instant date);
}
