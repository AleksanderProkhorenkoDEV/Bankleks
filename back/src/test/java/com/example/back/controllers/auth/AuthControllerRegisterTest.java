package com.example.back.controllers.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.example.back.dto.auth.RegisterRequestDTO;
import com.example.back.entities.user.User;

class AuthControllerRegisterTest extends AuthControllerBase {

    @Test
    void shouldReturnStringWhenFieldAreValid() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("usuario@gmail.com", "usuario", "123456789");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario registrado correctamente"));

    }

    @Test
    void shouldReturnBadRequestIfUserExist() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("usuario@gmail.com", "usuario", "123456789");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest())
                .andExpect(content().string("El email ya está registrado"));

    }
}
