package com.example.back.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.back.entities.auth.RefreshToken;
import com.example.back.entities.user.User;
import com.example.back.repositories.RefreshTokenRepository;
import com.example.back.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshExpirationMs", 60_000L);
    }

    @Test
    void shouldReturnRefreshTokenIfUserExist() {

        User user = new User("usuario", "usuario@gmail.com", "123456789");

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        assertNotNull(refreshToken);
        assertEquals(user, refreshToken.getUser());
        assertNotNull(refreshToken.getToken());
        assertFalse(refreshToken.getToken().isBlank());
        assertTrue(refreshToken.getExpiryDate().isAfter(Instant.now()));

        verify(userRepository).findByEmail(user.getEmail());
        verify(refreshTokenRepository).deleteByUser(user);
        verify(refreshTokenRepository).save(any(RefreshToken.class));

    }

    @Test
    void shouldReturnExceptionIfUserDoesExist() {
        User user = new User("usuario", "usuario@gmail.com", "123456789");

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> refreshTokenService.createRefreshToken(user.getEmail()));

        verify(refreshTokenRepository, never()).deleteByUser(any());
        verify(refreshTokenRepository, never()).save(any());

    }

    @Test
    void shouldReturnTrueIfExpirationDateIsBefore() {

        RefreshToken expiredToken = new RefreshToken(
                new User(),
                "expired-token",
                Instant.now().minusSeconds(60));

        boolean result = refreshTokenService.isExpired(expiredToken);

        assertTrue(result);

    }

    @Test
    void shouldReturnFalseIfExpirationDateIsAfter() {
        RefreshToken expiredToken = new RefreshToken(
                new User(),
                "valid-token",
                Instant.now().plusSeconds(60));

        boolean result = refreshTokenService.isExpired(expiredToken);

        assertFalse(result);
    }

    @Test
    void shouldDeleteRefreshTokens() {
        refreshTokenService.purgeExpiredTokens();

        verify(refreshTokenRepository)
                .deleteByExpiryDateBefore(any(Instant.class));

    }
}
