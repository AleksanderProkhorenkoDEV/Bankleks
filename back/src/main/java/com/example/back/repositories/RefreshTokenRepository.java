package com.example.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back.entities.auth.RefreshToken;
import java.util.Optional;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}
