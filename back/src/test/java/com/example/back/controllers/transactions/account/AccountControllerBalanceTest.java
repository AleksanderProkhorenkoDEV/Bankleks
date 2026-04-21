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
import com.example.back.entities.auth.Role;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.user.User;
import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(controllers = AccountController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class AccountControllerBalanceTest extends AccountControllerBase {

    Account account;
    User user;

    @BeforeEach
    void setUp() {
        user = new User("user", "user@gmail.com", "12345678", new Role());
        account = new Account(150.50, user, "1234567891234567899876");
        account.setId(1L);
    }

    @Test
    void shouldReturnBalanceIfAccountExist() throws Exception {

        when(accountService.getAccount(account.getId()))
                .thenReturn(account);

        mockMvc.perform(
                get("/accounts/{id}/balance", account.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(150.50)));
    }

    @Test
    void shouldReturnNotFoundIfAccountNotExist() throws Exception {
        Long nonExistentId = 99L;

        when(accountService.getAccount(nonExistentId))
                .thenThrow(new EntityNotFoundException());

        mockMvc.perform(get("/accounts/{id}/balance", nonExistentId))
                .andExpect(status().isNotFound());
    }
}
