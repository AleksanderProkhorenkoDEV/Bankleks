package com.example.back.dtos.mappers;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.back.dto.mappers.ScheduledTransferMapper;
import com.example.back.dto.transaction.transaction.ScheduledTransactionResponseDTO;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.ScheduledTransfer;
import com.example.back.enums.RecurrenceType;
import com.example.back.enums.ScheduledTransactionType;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduledTransferMapperTest {
    private ScheduledTransferMapper mapper;

    private static final Instant SCHEDULED_AT = Instant.parse("2024-03-01T10:00:00Z");
    private static final Instant RECURRENCE_END = Instant.parse("2024-12-31T00:00:00Z");

    @BeforeEach
    void setUp() {
        mapper = new ScheduledTransferMapper();
    }

    private Account buildAccount(Long id, String accountNumber) {
        Account account = new Account();
        account.setId(id);
        account.setAccountNumber(accountNumber);
        return account;
    }

    private ScheduledTransfer buildScheduledTransfer(Account origin, Account destination) {
        ScheduledTransfer transfer = new ScheduledTransfer(
                origin,
                destination,
                250.0,
                "Pago mensual",
                SCHEDULED_AT,
                "Europe/Madrid",
                RecurrenceType.BEGINNING_OF_MONTH,
                RECURRENCE_END,
                null,
                null);
        transfer.setId(1L);
        transfer.setStatus(ScheduledTransactionType.FAILED);
        return transfer;
    }

    @Test
    void toDto_shouldMapAllFields_whenBothAccountsPresent() {
        Account origin = buildAccount(1L, "ES1234567890");
        Account destination = buildAccount(2L, "ES0987654321");
        ScheduledTransfer transfer = buildScheduledTransfer(origin, destination);

        ScheduledTransactionResponseDTO dto = mapper.toDto(transfer);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getAmount()).isEqualTo(250.0);
        assertThat(dto.getConcept()).isEqualTo("Pago mensual");
        assertThat(dto.getTargetTimezone()).isEqualTo("Europe/Madrid");
        assertThat(dto.getStatus()).isEqualTo(ScheduledTransactionType.FAILED);
        assertThat(dto.getRecurrence()).isEqualTo(RecurrenceType.BEGINNING_OF_MONTH);
        assertThat(dto.getScheduledAt()).isEqualTo(SCHEDULED_AT);
        assertThat(dto.getRecurrenceEndDate()).isEqualTo(RECURRENCE_END);
    }

    @Test
    void toDto_shouldMapAccountOrigin_whenOriginPresent() {
        Account origin = buildAccount(1L, "ES1234567890");
        Account destination = buildAccount(2L, "ES0987654321");
        ScheduledTransfer transfer = buildScheduledTransfer(origin, destination);

        ScheduledTransactionResponseDTO dto = mapper.toDto(transfer);

        assertThat(dto.getAccountOrigin()).isNotNull();
        assertThat(dto.getAccountOrigin().getAccountNumber()).isEqualTo("ES1234567890");
    }

    @Test
    void toDto_shouldMapAccountDestination_whenDestinationPresent() {
        Account origin = buildAccount(1L, "ES1234567890");
        Account destination = buildAccount(2L, "ES0987654321");
        ScheduledTransfer transfer = buildScheduledTransfer(origin, destination);

        ScheduledTransactionResponseDTO dto = mapper.toDto(transfer);

        assertThat(dto.getAccountDestination()).isNotNull();
        assertThat(dto.getAccountDestination().getAccountNumber()).isEqualTo("ES0987654321");
    }

    @Test
    void toDto_shouldSetAccountOriginNull_whenOriginIsNull() {
        Account destination = buildAccount(2L, "ES0987654321");
        ScheduledTransfer transfer = buildScheduledTransfer(null, destination);

        ScheduledTransactionResponseDTO dto = mapper.toDto(transfer);

        assertThat(dto.getAccountOrigin()).isNull();
        assertThat(dto.getAccountDestination()).isNotNull();
    }

    @Test
    void toDto_shouldSetAccountDestinationNull_whenDestinationIsNull() {
        Account origin = buildAccount(1L, "ES1234567890");
        ScheduledTransfer transfer = buildScheduledTransfer(origin, null);

        ScheduledTransactionResponseDTO dto = mapper.toDto(transfer);

        assertThat(dto.getAccountDestination()).isNull();
        assertThat(dto.getAccountOrigin()).isNotNull();
    }

    @Test
    void toDto_shouldHandleNullRecurrence_whenRecurrenceIsNull() {
        Account origin = buildAccount(1L, "ES1234567890");
        Account destination = buildAccount(2L, "ES0987654321");
        ScheduledTransfer transfer = buildScheduledTransfer(origin, destination);
        transfer.setRecurrence(null);
        transfer.setRecurrenceEndDate(null);

        ScheduledTransactionResponseDTO dto = mapper.toDto(transfer);

        assertThat(dto.getRecurrence()).isNull();
        assertThat(dto.getRecurrenceEndDate()).isNull();
    }
}
