package com.example.back.dtos;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

import com.example.back.dto.transaction.account.AccountResponseDTO;
import com.example.back.dto.transaction.account.AccountStatsDTO;
import com.example.back.dto.transaction.account.BalancePointDTO;
import com.example.back.dto.user.UserSummaryDTO;

public class AccountResponseDTOTest {

    @Test
    void shouldCreateDTOWithCorrectValues() {
        List<BalancePointDTO> points = List.of(
                new BalancePointDTO(LocalDate.now(), 50D));

        AccountStatsDTO dto = new AccountStatsDTO(1500.0, 300.0, points);

        assertEquals(1500.0, dto.getTotalIncome());
        assertEquals(300.0, dto.getTotalExpense());
        assertEquals(1, dto.getBalancePointDTO().size());
    }

    @Test
    void shouldAllowSettingValues() {
        AccountStatsDTO dto = new AccountStatsDTO(0.0, 0.0, List.of());

        dto.setTotalIncome(500.0);
        dto.setTotalExpense(200.0);

        assertEquals(500.0, dto.getTotalIncome());
        assertEquals(200.0, dto.getTotalExpense());
    }

    @Test
    void shouldHandleEmptyList() {
        AccountStatsDTO dto = new AccountStatsDTO(0.0, 0.0, List.of());

        assertNotNull(dto.getBalancePointDTO());
        assertTrue(dto.getBalancePointDTO().isEmpty());
    }

    @Test
    void shouldReturnResponseDTO(){

        UserSummaryDTO user = new UserSummaryDTO(1L, "test@gmail.com" );
        new AccountResponseDTO("ES1234567899876543211593", 50.50, user);
    }
}
