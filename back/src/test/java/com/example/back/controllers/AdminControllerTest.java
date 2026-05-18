package com.example.back.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.back.dto.user.UserResponseDTO;
import com.example.back.services.UserService;

@SpringBootTest
public class AdminControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private AdminController adminController;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(adminController)
                .build();
    }

    @Test
    void shouldReturnPageOfUsers() throws Exception {
        UserResponseDTO user1 = new UserResponseDTO(1L, "test", "test@gmail.com", "CLIENT", "ES123", 50.0);
        UserResponseDTO user2 = new UserResponseDTO(2L, "test2", "other@gmail.com", "ADMINISTRATOR", "ES123", 75.0);

        var page = new PageImpl<>(List.of(user1, user2), PageRequest.of(0, 25), 2);
        when(userService.getAllUsers(0, 25)).thenReturn(page);

        mockMvc.perform(get("/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("test@gmail.com"))
                .andExpect(jsonPath("$.content[1].email").value("other@gmail.com"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void shouldUseDefaultPaginationParams() throws Exception {
        UserResponseDTO user1 = new UserResponseDTO(1L, "test", "test@gmail.com", "CLIENT", "ES123", 50.0);
        UserResponseDTO user2 = new UserResponseDTO(2L, "test2", "other@gmail.com", "ADMINISTRATOR", "ES123", 75.0);
        PageImpl<UserResponseDTO> page = new PageImpl<>(List.of(user1, user2), PageRequest.of(0, 25), 2);
        when(userService.getAllUsers(0, 25)).thenReturn(page);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk());

        verify(userService).getAllUsers(0, 25);
    }

    @Test
    void shouldUseCustomPaginationParams() throws Exception {
        PageImpl<UserResponseDTO> page = new PageImpl<>(List.of(), PageRequest.of(2, 10), 0);
        when(userService.getAllUsers(2, 10)).thenReturn(page);

        mockMvc.perform(get("/admin/users")
                .param("page", "2")
                .param("size", "10"))
                .andExpect(status().isOk());

        verify(userService).getAllUsers(2, 10);
    }

    @Test
    void shouldReturnEmptyPageWhenNoUsers() throws Exception {
        PageImpl<UserResponseDTO> page = new PageImpl<>(List.of(), PageRequest.of(0, 25), 0);
        when(userService.getAllUsers(0, 25)).thenReturn(page);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void shouldDeleteUserAndReturn200() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario eliminado"))
                .andExpect(jsonPath("$.status").value(200));
    }
}
