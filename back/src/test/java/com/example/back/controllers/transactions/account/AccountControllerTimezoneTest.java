package com.example.back.controllers.transactions.account;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import com.example.back.dto.transaction.account.AccountStatsDTO;
import com.example.back.dto.transaction.account.BalancePointDTO;
import com.example.back.entities.transactions.Account;

import jakarta.persistence.EntityNotFoundException;

public class AccountControllerTimezoneTest extends AccountControllerBase {
    private static final String BASE_URL = "/accounts";

    @Test
    @WithMockUser(username = "testuser@example.com")
    void getStats_shouldReturnStats_whenAuthenticated() throws Exception {
        List<BalancePointDTO> balancePoints = List.of(
                new BalancePointDTO(Instant.parse("2024-01-01T00:00:00Z"), 1000.0),
                new BalancePointDTO(Instant.parse("2024-02-01T00:00:00Z"), 1500.0));
        AccountStatsDTO statsDTO = new AccountStatsDTO(3000.0, 1500.0, balancePoints);

        when(accountService.getStats("testuser@example.com")).thenReturn(statsDTO);

        mockMvc.perform(get(BASE_URL + "/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(3000.0))
                .andExpect(jsonPath("$.totalExpense").value(1500.0))
                .andExpect(jsonPath("$.balancePointDTO[0].balance").value(1000.0))
                .andExpect(jsonPath("$.balancePointDTO[1].balance").value(1500.0));
    }


    @Test
    void getTimezone_shouldReturnTimezone_whenAccountExists() throws Exception {
        Account account = new Account();
        account.setTimezone("Europe/Madrid");

        when(accountService.getAccount(1L)).thenReturn(account);

        mockMvc.perform(get(BASE_URL + "/1/timezone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timezone").value("Europe/Madrid"));
    }

    @Test
    void getTimezone_shouldReturn404_whenAccountNotFound() throws Exception {
        when(accountService.getAccount(99L)).thenThrow(new EntityNotFoundException("Account not found"));

        mockMvc.perform(get(BASE_URL + "/99/timezone"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTimezone_shouldReturn204_whenValidTimezone() throws Exception {
        Map<String, String> body = Map.of("timezone", "America/New_York");
        doNothing().when(accountService).updateTimezone(1L, "America/New_York");

        mockMvc.perform(patch(BASE_URL + "/1/timezone")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNoContent());

        verify(accountService).updateTimezone(1L, "America/New_York");
    }

    @Test
    void updateTimezone_shouldReturn404_whenAccountNotFound() throws Exception {
        Map<String, String> body = Map.of("timezone", "America/New_York");
        doThrow(new EntityNotFoundException("Account not found"))
                .when(accountService).updateTimezone(99L, "America/New_York");

        mockMvc.perform(patch(BASE_URL + "/99/timezone")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTimezone_shouldReturn400_whenTimezoneKeyMissing() throws Exception {
        Map<String, String> body = Map.of("wrong_key", "America/New_York");
        doThrow(new IllegalArgumentException("Timezone is required"))
                .when(accountService).updateTimezone(1L, null);

        mockMvc.perform(patch(BASE_URL + "/1/timezone")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}
