package com.example.back.controllers.transactions.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.example.back.entities.auth.Role;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.user.User;

public class AccountTest {
    User user = new User("user", "user@gmail.com", "12345678", new Role());

    @Test
    void shouldThrowIfBalanceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Account(null, "1234567891234567899876", "UTC", user));
    }

    @Test
    void shouldThrowIfUserIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Account(150.50, "1234567891234567899876", "UTC", null));
    }

    @Test
    void shouldThrowIfAccountNumberIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Account(150.50, null, "UTC", user));
    }

    @Test
    void shouldCreateAccountWithValidData() {
        Account account = new Account(150.50, "1234567891234567899876", "UTC", user);
        assertEquals(150.50, account.getBalance());
        assertEquals(user, account.getUser());
        assertEquals("1234567891234567899876", account.getAccountNumber());
    }
}
