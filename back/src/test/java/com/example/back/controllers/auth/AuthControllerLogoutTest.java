package com.example.back.controllers.auth;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.example.back.dto.auth.LogoutRequestDTO;
import com.example.back.entities.auth.RefreshToken;

class AuthControllerLogoutTest extends AuthControllerBase {

    @Test
    void shouldReturn400WhenRefreshTokenIsInvalid() throws Exception {

        LogoutRequestDTO request = new LogoutRequestDTO("refresh-token-fake");

        when(refreshTokenRepository.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Refresh token inválido."));

    }

    @Test
    void shouldReturn400WhenFieldAreInvalid() throws Exception {
        LogoutRequestDTO request = new LogoutRequestDTO("");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200WhenRefreshTokenIsValid() throws Exception {
        LogoutRequestDTO request = new LogoutRequestDTO("refresh-token-fake");

        RefreshToken token = new RefreshToken();
        token.setToken("refresh-token-valid");

        when(refreshTokenRepository.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.of(token));

        mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Sesión cerrada"));

    }
}
