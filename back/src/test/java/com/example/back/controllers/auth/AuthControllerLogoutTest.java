package com.example.back.controllers.auth;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.example.back.entities.auth.RefreshToken;

import jakarta.servlet.http.Cookie;

class AuthControllerLogoutTest extends AuthControllerBase {
        @Test
        void shouldReturn400WhenRefreshTokenIsInvalid() throws Exception {

                when(refreshTokenRepository.findByToken("refresh-token-fake"))
                                .thenReturn(Optional.empty());

                mockMvc.perform(post("/auth/logout")
                                .cookie(new Cookie("refreshToken", "refresh-token-fake")))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message")
                                                .value("Refresh token inválido."));
        }

        @Test
        void shouldReturn400WhenCookieIsMissing() throws Exception {

                mockMvc.perform(post("/auth/logout"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn200WhenRefreshTokenIsValid() throws Exception {

                RefreshToken token = new RefreshToken();
                token.setToken("refresh-token-valid");

                when(refreshTokenRepository.findByToken("refresh-token-valid"))
                                .thenReturn(Optional.of(token));

                mockMvc.perform(post("/auth/logout")
                                .cookie(new Cookie("refreshToken", "refresh-token-valid")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message")
                                                .value("Sesión cerrada"));
        }
}
