package com.example.back.controllers.transactions.account;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import com.example.back.controllers.AccountController;
import com.example.back.dto.transaction.account.AccountResponseDTO;
import com.example.back.dto.user.UserSummaryDTO;
import com.example.back.entities.auth.Role;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.user.User;
import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(controllers = AccountController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class AccountControllerBalanceTest extends AccountControllerBase {

    Account account;
    User user;

    private AccountResponseDTO accountResponseDTO;

    @BeforeEach
    void setUp() {
        User user = new User("test", "test@gmail.com", "123456", new Role());
        user.setId(1L);
        account = new Account(500.0, user, "ES1234567890");
        account.setId(1L);

        accountResponseDTO = new AccountResponseDTO(
                "ES1234567890", 500.0,
                new UserSummaryDTO(1L, "test"));
    }

    @Test
    void shouldReturnBalanceWhenAccountExists() throws Exception {
        when(accountService.getAccount(1L)).thenReturn(account);

        mockMvc.perform(get("/accounts/{id}/balance", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(500.0)));
    }

    @Test
    void shouldReturnNotFoundWhenAccountNotExists() throws Exception {
        when(accountService.getAccount(99L)).thenThrow(new EntityNotFoundException());

        mockMvc.perform(get("/accounts/{id}/balance", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBalanceIfAccountExist() throws Exception {

        when(accountService.getAccount(account.getId()))
                .thenReturn(account);

        mockMvc.perform(
                get("/accounts/{id}/balance", account.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(500.0)));
    }

    @Test
    void shouldReturnNotFoundIfAccountNotExist() throws Exception {
        Long nonExistentId = 99L;

        when(accountService.getAccount(nonExistentId))
                .thenThrow(new EntityNotFoundException());

        mockMvc.perform(get("/accounts/{id}/balance", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAccountDataWhenAccountExists() throws Exception {
        when(accountService.getAccount(1L)).thenReturn(account);
        when(accountMapper.toDTO(account)).thenReturn(accountResponseDTO);

        mockMvc.perform(get("/accounts/{id}/all-data", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber", is("ES1234567890")))
                .andExpect(jsonPath("$.balance", is(500.0)))
                .andExpect(jsonPath("$.userSummaryDTO.username", is("test")));
    }

}
