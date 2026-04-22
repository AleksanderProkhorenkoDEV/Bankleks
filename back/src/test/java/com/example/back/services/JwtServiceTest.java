package com.example.back.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.back.config.JwtProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("my-super-secret-key-my-super-secret-key");

        jwtService = new JwtService(props);
    }

    @Test
    void shouldGenerateValidJwtToken() {
        String token = jwtService.generateToken("user@test.com", "CLIENT");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractSubjectFromToken() {
        String token = jwtService.generateToken("user@test.com", "CLIENT");

        String subject = jwtService.extractSubject(token);

        assertEquals("user@test.com", subject);
    }

    @Test
    void shouldReturnTrueForValidTokenAndCorrectSubject() {
        String token = jwtService.generateToken("user@test.com", "CLIENT");

        boolean valid = jwtService.isTokenValid(token, "user@test.com");

        assertTrue(valid);
    }

    @Test
    void shouldReturnFalseWhenSubjectDoesNotMatch() {
        String token = jwtService.generateToken("user@test.com", "CLIENT");

        boolean valid = jwtService.isTokenValid(token, "other@test.com");

        assertFalse(valid);
    }

    @Test
    void shouldReturnFalseForExpiredToken() {
        String expiredToken = Jwts.builder()
                .setSubject("user@test.com")
                .setIssuedAt(new Date(0))
                .setExpiration(new Date(1000))
                .signWith(
                        Keys.hmacShaKeyFor("my-super-secret-key-my-super-secret-key".getBytes()),
                        SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtService.isTokenValid(expiredToken, "user@test.com"));
    }

    @Test
    void shouldReturnFalseForMalformedToken() {
        assertFalse(jwtService.isTokenValid("esto.no.es.un.token", "user@test.com"));
    }

}
