package com.example.back.controllers.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;

import com.example.back.dto.auth.LoginRequestDTO;
import com.example.back.dto.auth.LoginResponseDTO;
import com.example.back.entities.auth.RefreshToken;

import static org.hamcrest.Matchers.is;

class AuthControllerLoginTest extends AuthControllerBase {

        @Test
        void shouldReturnTokenWhenCredentialsAreCorrect() throws Exception {
                LoginRequestDTO request = new LoginRequestDTO("usuario@gmail.com", "123456");

                RefreshToken mockRefreshToken = new RefreshToken();
                mockRefreshToken.setToken("refresh-token-123");

                LoginResponseDTO mockResponse = new LoginResponseDTO(
                                "jwt-falso", "usuario@gmail.com", "CLIENT", 1L, "1234567890");

                when(authService.login(any(), any())).thenReturn(mockResponse);

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token", is("jwt-falso")))
                                .andExpect(jsonPath("$.userName", is("usuario@gmail.com")));
        }

        @Test
        void shouldReturn400WhenFieldsAreInvalid() throws Exception {
                LoginRequestDTO request = new LoginRequestDTO("no-es-un-email", "");

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn401WhenCredentialsAreWrong() throws Exception {
                LoginRequestDTO request = new LoginRequestDTO("usuario@gmail.com", "wrongpassword");

                when(authService.login(any(), any()))
                                .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized()); 
        }
}