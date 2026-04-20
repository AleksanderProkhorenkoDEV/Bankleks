package com.example.back.controllers.transactions.account;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.security.auth.login.AccountNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.back.entities.transactions.Account;

public class AccountControllerBalance extends AccountControllerBase {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setBalance(150.50);
        account.setId(1L);
    }

    @Test
    void shouldReturnBalanceIfAccountExist() throws Exception {
        when(accountService.getAccount(account.getId()))
                .thenReturn(account);

        mockMvc.perform(get("/accounts/{id}/balance", account.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150.50));
    }

    @Test
    void shouldReturnNotFoundIfAccountNotExist() throws Exception {
        Long nonExistentId = 99L;

        when(accountService.getAccount(nonExistentId))
                .thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(get("/accounts/{id}/balance", nonExistentId))
                .andExpect(status().isNotFound());
    }
}
