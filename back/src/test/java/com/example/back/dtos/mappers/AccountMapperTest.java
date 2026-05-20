package com.example.back.dtos.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.back.dto.mappers.AccountMapper;
import com.example.back.dto.transaction.account.AccountResponseDTO;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AccountMapperTest {
    private AccountMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AccountMapper();
    }

    private User buildUser(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        return user;
    }

    private Account buildAccount(String accountNumber, Double balance, User user) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setUser(user);
        return account;
    }

    @Test
    void toDTO_shouldMapAllFields_whenAccountIsValid() {
        User user = buildUser(1L, "Juan García");
        Account account = buildAccount("ES1234567890", 1500.0, user);

        AccountResponseDTO dto = mapper.toDTO(account);

        assertThat(dto.getAccountNumber()).isEqualTo("ES1234567890");
        assertThat(dto.getBalance()).isEqualTo(1500.0);
    }

    @Test
    void toDTO_shouldMapUserSummary_whenUserIsPresent() {
        User user = buildUser(1L, "Juan García");
        Account account = buildAccount("ES1234567890", 1500.0, user);

        AccountResponseDTO dto = mapper.toDTO(account);

        assertThat(dto.getUserSummaryDTO()).isNotNull();
        assertThat(dto.getUserSummaryDTO().getId()).isEqualTo(1L);
        assertThat(dto.getUserSummaryDTO().getUsername()).isEqualTo("Juan García");
    }

    @Test
    void toDTO_shouldMapZeroBalance_whenBalanceIsZero() {
        User user = buildUser(2L, "María López");
        Account account = buildAccount("ES0987654321", 0.0, user);

        AccountResponseDTO dto = mapper.toDTO(account);

        assertThat(dto.getBalance()).isEqualTo(0.0);
        assertThat(dto.getAccountNumber()).isEqualTo("ES0987654321");
    }

    @Test
    void toDTO_shouldThrowNullPointerException_whenUserIsNull() {
        Account account = buildAccount("ES1234567890", 1500.0, null);

        assertThatThrownBy(() -> mapper.toDTO(account))
                .isInstanceOf(NullPointerException.class);
    }
}
