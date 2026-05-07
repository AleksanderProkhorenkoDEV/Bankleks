package com.example.back.controllers.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.back.entities.auth.RefreshToken;
import com.example.back.entities.auth.Role;
import com.example.back.entities.user.User;

import jakarta.servlet.http.Cookie;

class AuthControllerRefreshTest extends AuthControllerBase {

        private User user;
        private Role role;

        @BeforeEach
        void setUp() {
                role = new Role();
                role.setName("CLIENT");
                user = new User();
                user.setEmail("usuario@gmail.com");
                user.setRole(role);
        }

        @Test
        void shouldReturn200WhenRefreshTokenIsValid() throws Exception {
                RefreshToken token = new RefreshToken();
                token.setToken("refresh-token-valid");
                token.setUser(user);

                when(refreshTokenRepository.findByToken("refresh-token-valid"))
                                .thenReturn(Optional.of(token));
                when(refreshTokenService.isExpired(token)).thenReturn(false);
                when(jwtService.generateToken("usuario@gmail.com", "CLIENT"))
                                .thenReturn("new-jwt-token");

                mockMvc.perform(post("/auth/refresh")
                                .cookie(new Cookie("refreshToken", "refresh-token-valid")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Token actualizado"))
                                .andExpect(jsonPath("$.newAccessToken").value("new-jwt-token"));
        }

        @Test
        void shouldReturn400WhenRefreshTokenIsExpired() throws Exception {
                RefreshToken token = new RefreshToken();
                token.setToken("refresh-token-expired");
                token.setUser(user);

                when(refreshTokenRepository.findByToken("refresh-token-expired"))
                                .thenReturn(Optional.of(token));
                when(refreshTokenService.isExpired(token)).thenReturn(true);

                mockMvc.perform(post("/auth/refresh")
                                .cookie(new Cookie("refreshToken", "refresh-token-expired")))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message")
                                                .value("Refresh token expirado, vuelve a hacer login."));

                verify(refreshTokenRepository).delete(token);
                verify(jwtService, never()).generateToken(anyString(), anyString());
        }

        @Test
        void shouldReturn400WhenRefreshTokenIsInvalid() throws Exception {
                when(refreshTokenRepository.findByToken("refresh-token-invalid"))
                                .thenReturn(Optional.empty());

                mockMvc.perform(post("/auth/refresh")
                                .cookie(new Cookie("refreshToken", "refresh-token-invalid")))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Refresh token inválido."));

                verify(refreshTokenRepository, never()).delete(any());
                verify(jwtService, never()).generateToken(anyString(), anyString());
        }
}
