package com.example.back.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.example.back.entities.auth.RefreshToken;
import com.example.back.entities.user.User;

public class RefreshTokenTest {

    @Test
    void shouldCreateRefreshTokenWhenAllArgumentsAreValid() {
        User user = new User("nombre", "email@test.com", "pwd");
        Instant expiry = Instant.now().plusSeconds(60);

        RefreshToken token = new RefreshToken(user, "refresh-token", expiry);

        assertEquals(user, token.getUser());
        assertEquals("refresh-token", token.getToken());
        assertEquals(expiry, token.getExpiryDate());
    }

    @Test
    void shouldThrowExceptionWhenExpiryDateIsNull() {
        User user = new User("nombre", "email@test.com", "pwd");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new RefreshToken(user, "token", null));

        assertEquals("La fecha de expiración no puede ser nula.", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTokenIsNull() {
        User user = new User("nombre", "email@test.com", "pwd");

        assertThrows(IllegalArgumentException.class, () -> new RefreshToken(user, null, Instant.now()));
    }

    @Test
    void shouldThrowExceptionWhenTokenIsBlank() {
        User user = new User("nombre", "email@test.com", "pwd");

        assertThrows(IllegalArgumentException.class, () -> new RefreshToken(user, "", Instant.now()));
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new RefreshToken(null, "token", Instant.now()));

        assertEquals("El usuario no puede ser nulo", ex.getMessage());
    }

}
