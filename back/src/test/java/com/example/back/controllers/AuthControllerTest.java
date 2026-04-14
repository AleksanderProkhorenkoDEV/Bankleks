package com.example.back.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.back.dto.auth.LoginRequestDTO;
import com.example.back.entities.auth.RefreshToken;
import com.example.back.repositories.RefreshTokenRepository;
import com.example.back.repositories.UserRepository;
import com.example.back.services.JwtService;
import com.example.back.services.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void shouldReturnTokenWhenCredentialsAreCorrect() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("usuario@gmail.com", "123456");

        UserDetails userDetails = User.withUsername("usuario@gmail.com")
                .password("123456")
                .authorities("ROLE_USER")
                .build();

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("refresh-token-123");

        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()));

        when(jwtService.generateToken("usuario@gmail.com")).thenReturn("jwt-falso");
        when(refreshTokenService.createRefreshToken("usuario@gmail.com")).thenReturn(mockRefreshToken);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("jwt-falso")))
                .andExpect(jsonPath("$.refreshToken", is("refresh-token-123")))
                .andExpect(jsonPath("$.email", is("usuario@gmail.com")));
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
        LoginRequestDTO request = new LoginRequestDTO("usuario@gmail.com", "contraseña-incorrecta");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException(
                        "Credenciales incorrectas"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}