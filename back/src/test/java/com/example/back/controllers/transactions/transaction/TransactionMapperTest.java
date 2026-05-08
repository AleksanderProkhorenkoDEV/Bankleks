package com.example.back.controllers.transactions.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.example.back.dto.mappers.TransactionMapper;
import com.example.back.entities.auth.Role;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.Transaction;
import com.example.back.entities.user.User;
import com.example.back.enums.TransactionType;

public class TransactionMapperTest {
    private final TransactionMapper mapper = new TransactionMapper();
    private final User user = new User("test", "test@gmail.com", "123456789", new Role());
    private final Account destination = new Account(150.50, "1234567899876543211234", "UTC", user);
    private final Account origin = new Account(350.69, "9876543211234567899516", "UTC", user);

    @Test
    void shouldMapTransactionToDto() {
        Transaction transaction = new Transaction(
                "test concept", 6.99, Instant.now(),
                TransactionType.DEPOSIT, user, destination, null, null);
        transaction.setId(1L);

        var dto = mapper.toDto(transaction);

        assertEquals(1L, dto.getId());
        assertEquals(6.99, dto.getAmount());
        assertEquals("test concept", dto.getConcept());
        assertEquals(TransactionType.DEPOSIT, dto.getTransactionType());
        assertNotNull(dto.getDestinationAccount());
        assertNull(dto.getOriginAccount());
        assertNotNull(dto.getUser());
    }

    @Test
    void shouldMapOriginAndDestinationAccounts() {
        Transaction transaction = new Transaction(
                "transfer", 100.0, Instant.now(),
                TransactionType.TRANSFER, user, destination, origin, null);

        var dto = mapper.toDto(transaction);

        assertNotNull(dto.getOriginAccount());
        assertNotNull(dto.getDestinationAccount());
        assertEquals(destination.getAccountNumber(), dto.getDestinationAccount().getAccountNumber());
        assertEquals(origin.getAccountNumber(), dto.getOriginAccount().getAccountNumber());
    }

    @Test
    void shouldReturnNullAccountSummaryIfAccountIsNull() {
        Transaction transaction = new Transaction(
                "withdrawal", 50.0, Instant.now(),
                TransactionType.WITHDRAWAL, user, null, origin, null);

        var dto = mapper.toDto(transaction);

        assertNull(dto.getDestinationAccount());
        assertNotNull(dto.getOriginAccount());
    }
}
