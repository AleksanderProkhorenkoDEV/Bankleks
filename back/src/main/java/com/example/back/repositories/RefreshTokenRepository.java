package com.example.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back.entities.auth.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {}
