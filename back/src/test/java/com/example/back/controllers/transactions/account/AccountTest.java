package com.example.back.controllers.transactions.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.example.back.entities.auth.Role;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.user.User;

public class AccountTest {
    User user = new User("user", "user@gmail.com", "12345678", new Role());

    @Test
    void shouldCreateAccountWithValidData() {
        Account account = new Account(150.50, "1234567891234567899876", "UTC", user);
        assertEquals(150.50, account.getBalance());
        assertEquals(user, account.getUser());
        assertEquals("1234567891234567899876", account.getAccountNumber());
    }
}
