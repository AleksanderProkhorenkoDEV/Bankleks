package com.example.back.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.example.back.dto.user.UserResponseDTO;
import com.example.back.entities.auth.Role;
import com.example.back.entities.user.User;
import com.example.back.repositories.RefreshTokenRepository;
import com.example.back.repositories.TransactionRepository;
import com.example.back.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("CLIENT");
        user = new User("test", "test@gmail.com", "123456", role);
        user.setId(1L);
    }

    // --- getUser(Long) ---

    @Test
    void shouldReturnUserWhenFoundById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUser(1L);

        assertEquals(user, result);
    }

    @Test
    void shouldThrowWhenUserNotFoundById() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(99L));
    }

    // --- getUser(String) ---

    @Test
    void shouldReturnUserWhenFoundByEmail() {
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        User result = userService.getUser("test@gmail.com");

        assertEquals(user, result);
    }

    @Test
    void shouldThrowWhenUserNotFoundByEmail() {
        when(userRepository.findByEmail("noexiste@gmail.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUser("noexiste@gmail.com"));
    }

    // --- getAllUsers ---

    @Test
    void shouldReturnPageOfUserResponseDTOs() {
        User user2 = new User("other", "other@gmail.com", "123456", role);
        user2.setId(2L);

        Page<User> userPage = new PageImpl<>(List.of(user, user2), PageRequest.of(0, 10), 2);
        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(userPage);

        Page<UserResponseDTO> result = userService.getAllUsers(0, 10);

        assertEquals(2, result.getTotalElements());
        assertEquals("test@gmail.com", result.getContent().get(0).getEmail());
        assertEquals("other@gmail.com", result.getContent().get(1).getEmail());
        assertEquals("CLIENT", result.getContent().get(0).getRole());
    }

    @Test
    void shouldReturnEmptyPageWhenNoUsers() {
        Page<User> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(emptyPage);

        Page<UserResponseDTO> result = userService.getAllUsers(0, 10);

        assertTrue(result.isEmpty());
    }

    // --- deleteUser ---

    @Test
    void shouldDeleteUserAndCleanRelatedData() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(refreshTokenRepository).deleteByUser(user);
        verify(transactionRepository).nullifyUserReferences(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(99L));

        verify(refreshTokenRepository, never()).deleteByUser(any());
        verify(transactionRepository, never()).nullifyUserReferences(any());
        verify(userRepository, never()).delete(any(User.class));
    }
}
