package com.example.back.controllers.transactions.account;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;

import com.example.back.controllers.AccountController;
import com.example.back.dto.transaction.account.CreateAccountRequestDTO;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.user.User;

@WebMvcTest(controllers = AccountController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class AccountControllerCreateTest extends AccountControllerBase {

    @Test
    void shouldCreateAccount() throws Exception {
        CreateAccountRequestDTO request = new CreateAccountRequestDTO("1234567899876543211234", 1L, 150.50);
        Account account = new Account(150.50, new User(), "1234567899876543211234");

        when(accountService.createAccount(request)).thenReturn(account);

        mockMvc.perform(post("/accounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Cuenta creada correctamente")))
                .andExpect(jsonPath("$.status", is(200)));
    }

    @Test
    void shouldReturnBadRequestIfAccountNumberIsNull() throws Exception {
        CreateAccountRequestDTO request = new CreateAccountRequestDTO(null, 1L, 150.50);

        mockMvc.perform(post("/accounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfAccountNumberIsTooShort() throws Exception {
        CreateAccountRequestDTO request = new CreateAccountRequestDTO("12345", 1L, 150.50);

        mockMvc.perform(post("/accounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfUserIdIsNull() throws Exception {
        CreateAccountRequestDTO request = new CreateAccountRequestDTO("1234567899876543211234", null, 150.50);

        mockMvc.perform(post("/accounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfBalanceIsNull() throws Exception {
        CreateAccountRequestDTO request = new CreateAccountRequestDTO("1234567899876543211234", 1L, null);

        mockMvc.perform(post("/accounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfBalanceIsNegative() throws Exception {
        CreateAccountRequestDTO request = new CreateAccountRequestDTO("1234567899876543211234", 1L, -50.0);

        mockMvc.perform(post("/accounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
