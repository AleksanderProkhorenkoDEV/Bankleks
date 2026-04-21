package com.example.back.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.back.entities.auth.Role;
import com.example.back.entities.user.User;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    @Mock
    private Role role;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldReturnUserDetailsWhenUserExists() {

        when(role.getName()).thenReturn("USER");

        User user = new User("usuario", "usuario@gmail.com", "encoded-password", role);

        when(userService.getUser(user.getEmail())).thenReturn(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername("usuario@gmail.com");

        assertNotNull(userDetails);
        assertEquals("usuario@gmail.com", userDetails.getUsername());
        assertEquals("encoded-password", userDetails.getPassword());
        assertTrue(
                userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        verify(userService).getUser("usuario@gmail.com");
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        when(userService.getUser("missing@gmail.com")).thenThrow(new UsernameNotFoundException("Usuario no encontrado"));

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("missing@gmail.com"));

        verify(userService).getUser("missing@gmail.com");
    }

}
