package com.example.back.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.back.entities.user.User;
import com.example.back.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldReturnUserDetailsWhenUserExists() {
        User user = new User("usuario", "usuario@gmail.com", "encoded-password");

        when(userRepository.findByEmail("usuario@gmail.com"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("usuario@gmail.com");

        assertNotNull(userDetails);
        assertEquals("usuario@gmail.com", userDetails.getUsername());
        assertEquals("encoded-password", userDetails.getPassword());
        assertTrue(
                userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        verify(userRepository).findByEmail("usuario@gmail.com");
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findByEmail("missing@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("missing@gmail.com"));

        verify(userRepository).findByEmail("missing@gmail.com");
    }

}
