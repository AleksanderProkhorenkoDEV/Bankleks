package com.example.back.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.back.dto.transaction.account.CreateAccountRequestDTO;
import com.example.back.entities.auth.Role;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.user.User;
import com.example.back.repositories.AccountRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        user = new User("test", "test@gmail.com", "123456789", new Role());
        user.setId(1L);
        account = new Account(150.50, user, "1234567899876543211234");
        account.setId(1L);
    }


    @Test
    void shouldReturnAccountById() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Account result = accountService.getAccount(1L);

        assertEquals(account, result);
    }

    @Test
    void shouldThrowIfAccountNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.getAccount(99L));
    }

    @Test
    void shouldCreateAccount() {
        CreateAccountRequestDTO request = new CreateAccountRequestDTO(
                "1234567899876543211234", user.getId(), 150.50);

        when(userService.getUser(user.getId())).thenReturn(user);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = accountService.createAccount(request);

        assertNotNull(result);
        verify(userService).getUser(user.getId());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldAddBalance() {
        accountService.addBalance(account, 50.0);

        assertEquals(200.50, account.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void shouldSubtractBalance() {
        accountService.subtractBalance(account, 50.0);

        assertEquals(100.50, account.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void shouldThrowIfInsufficientFunds() {
        assertThrows(RuntimeException.class, () -> accountService.subtractBalance(account, 200.0));

        verify(accountRepository, never()).save(any());
    }
}
