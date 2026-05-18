package com.example.back.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.back.entities.auth.Role;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.ScheduledTransfer;
import com.example.back.entities.transactions.Transaction;
import com.example.back.entities.user.User;
import com.example.back.enums.RecurrenceType;
import com.example.back.enums.ScheduledTransactionType;
import com.example.back.repositories.ScheduledTransferRepository;

@ExtendWith(MockitoExtension.class)
public class TransactonScheduledServiceTest {
    @InjectMocks
    private TransactionScheduledService transactionScheduledService;

    @Mock
    private ScheduledTransferRepository scheduledTransferRepository;

    private User user;
    private Account origin;
    private Account destination;
    private ScheduledTransfer scheduledTransfer;

    @BeforeEach
    void setUp() {
        user = new User("test", "test@gmail.com", "123456789", new Role());
        user.setId(1L);
        origin = new Account(1000.0, "9876543211234567899516", "UTC", user);
        origin.setId(1L);
        destination = new Account(500.0, "1234567899876543211234", "UTC", user);
        destination.setId(2L);

        // 2026-01-15T10:00:00Z — fecha fija para cálculos predecibles
        Instant fixedDate = Instant.parse("2026-01-15T10:00:00Z");
        scheduledTransfer = new ScheduledTransfer(origin, destination, 100.0, "pago",
                fixedDate, "UTC", RecurrenceType.BEGINNING_OF_MONTH, null);
    }

    // ─────────────────────────────────────────────
    // createScheduledTransfer
    // ─────────────────────────────────────────────

    @Test
    void shouldSaveNewScheduledTransfer() {
        Instant scheduledAt = Instant.now().plus(1, ChronoUnit.DAYS);

        transactionScheduledService.createScheduledTransfer(
                origin, destination, 50.0, "concepto", scheduledAt, "UTC",
                RecurrenceType.MIDDLE_OF_MONTH, null);

        ArgumentCaptor<ScheduledTransfer> captor = ArgumentCaptor.forClass(ScheduledTransfer.class);
        verify(scheduledTransferRepository).save(captor.capture());

        ScheduledTransfer saved = captor.getValue();
        assertEquals(origin, saved.getAccountOrigin());
        assertEquals(destination, saved.getAccountDestination());
        assertEquals(50.0, saved.getAmount());
        assertEquals("concepto", saved.getConcept());
        assertEquals(scheduledAt, saved.getScheduledAt());
        assertEquals("UTC", saved.getTargetTimezone());
        assertEquals(RecurrenceType.MIDDLE_OF_MONTH, saved.getRecurrence());
        assertNull(saved.getRecurrenceEndDate());
    }

    // ─────────────────────────────────────────────
    // getPendingTransfers
    // ─────────────────────────────────────────────

    @Test
    void shouldReturnPendingTransfers() {
        List<ScheduledTransfer> pending = List.of(scheduledTransfer);
        when(scheduledTransferRepository.findByStatusAndScheduledAtBefore(
                eq(ScheduledTransactionType.SCHEDULED), any(Instant.class)))
                .thenReturn(pending);

        List<ScheduledTransfer> result = transactionScheduledService.getPendingTransfers();

        assertEquals(1, result.size());
        assertEquals(scheduledTransfer, result.get(0));
    }

    @Test
    void shouldReturnEmptyListWhenNoPendingTransfers() {
        when(scheduledTransferRepository.findByStatusAndScheduledAtBefore(
                eq(ScheduledTransactionType.SCHEDULED), any(Instant.class)))
                .thenReturn(List.of());

        List<ScheduledTransfer> result = transactionScheduledService.getPendingTransfers();

        assertTrue(result.isEmpty());
    }

    // ─────────────────────────────────────────────
    // markAsExecuting
    // ─────────────────────────────────────────────

    @Test
    void shouldMarkTransferAsExecuting() {
        transactionScheduledService.markAsExecuting(scheduledTransfer);

        assertEquals(ScheduledTransactionType.EXECUTING, scheduledTransfer.getStatus());
        verify(scheduledTransferRepository).save(scheduledTransfer);
    }

    // ─────────────────────────────────────────────
    // markAsExecuted
    // ─────────────────────────────────────────────

    @Test
    void shouldMarkTransferAsExecutedAndLinkTransaction() {
        Transaction transaction = mock(Transaction.class);

        transactionScheduledService.markAsExecuted(scheduledTransfer, transaction);

        assertEquals(ScheduledTransactionType.EXECUTED, scheduledTransfer.getStatus());
        assertEquals(transaction, scheduledTransfer.getTransaction());
        verify(scheduledTransferRepository).save(scheduledTransfer);
    }

    // ─────────────────────────────────────────────
    // markAsFailed
    // ─────────────────────────────────────────────

    @Test
    void shouldMarkTransferAsFailed() {
        transactionScheduledService.markAsFailed(scheduledTransfer);

        assertEquals(ScheduledTransactionType.FAILED, scheduledTransfer.getStatus());
        verify(scheduledTransferRepository).save(scheduledTransfer);
    }

    // ─────────────────────────────────────────────
    // hasNextRecurrence
    // ─────────────────────────────────────────────

    @Test
    void shouldReturnFalseWhenRecurrenceIsNull() {
        ScheduledTransfer noRecurrence = new ScheduledTransfer(origin, destination, 100.0, "pago",
                Instant.now(), "UTC", null, null);

        assertFalse(transactionScheduledService.hasNextRecurrence(noRecurrence));
    }

    @Test
    void shouldReturnTrueWhenRecurrenceEndDateIsNull() {
        // scheduledTransfer del setUp tiene recurrenceEndDate = null
        assertTrue(transactionScheduledService.hasNextRecurrence(scheduledTransfer));
    }

    @Test
    void shouldReturnTrueWhenNextExecutionIsBeforeEndDate() {
        // scheduledAt: 2026-01-15, next: 2026-02-01, endDate: 2026-06-01 → debe
        // continuar
        Instant endDate = Instant.parse("2026-06-01T00:00:00Z");
        scheduledTransfer.setRecurrenceEndDate(endDate);

        assertTrue(transactionScheduledService.hasNextRecurrence(scheduledTransfer));
    }

    @Test
    void shouldReturnFalseWhenNextExecutionIsAfterEndDate() {
        // scheduledAt: 2026-01-15, next: 2026-02-01, endDate: 2026-01-20 → no continúa
        Instant endDate = Instant.parse("2026-01-20T00:00:00Z");
        scheduledTransfer.setRecurrenceEndDate(endDate);

        assertFalse(transactionScheduledService.hasNextRecurrence(scheduledTransfer));
    }

    // ─────────────────────────────────────────────
    // calculateNextExecution
    // ─────────────────────────────────────────────

    @Test
    void shouldCalculateNextExecutionForBeginningOfMonth() {
        // scheduledAt: 2026-01-15 → next: 2026-02-01T10:00:00Z
        Instant next = transactionScheduledService.calculateNextExecution(scheduledTransfer);

        assertEquals(Instant.parse("2026-02-01T10:00:00Z"), next);
    }

    @Test
    void shouldCalculateNextExecutionForMiddleOfMonth() {
        scheduledTransfer.setRecurrence(RecurrenceType.MIDDLE_OF_MONTH);
        // scheduledAt: 2026-01-15 → next: 2026-02-15T10:00:00Z
        Instant next = transactionScheduledService.calculateNextExecution(scheduledTransfer);

        assertEquals(Instant.parse("2026-02-15T10:00:00Z"), next);
    }

    @Test
    void shouldCalculateNextExecutionForEndOfMonth() {
        scheduledTransfer.setRecurrence(RecurrenceType.END_OF_MONTH);
        // scheduledAt: 2026-01-15 → next: 2026-02-28T10:00:00Z (febrero 2026 no es
        // bisiesto)
        Instant next = transactionScheduledService.calculateNextExecution(scheduledTransfer);

        assertEquals(Instant.parse("2026-02-28T10:00:00Z"), next);
    }

    // ─────────────────────────────────────────────
    // scheduleNextRecurrence
    // ─────────────────────────────────────────────

    @Test
    void shouldSaveNextRecurrenceWithCalculatedDate() {
        transactionScheduledService.scheduleNextRecurrence(scheduledTransfer);

        ArgumentCaptor<ScheduledTransfer> captor = ArgumentCaptor.forClass(ScheduledTransfer.class);
        verify(scheduledTransferRepository).save(captor.capture());

        ScheduledTransfer next = captor.getValue();
        assertEquals(Instant.parse("2026-02-01T10:00:00Z"), next.getScheduledAt());
        assertEquals(origin, next.getAccountOrigin());
        assertEquals(destination, next.getAccountDestination());
        assertEquals(100.0, next.getAmount());
        assertEquals("pago", next.getConcept());
        assertEquals("UTC", next.getTargetTimezone());
        assertEquals(RecurrenceType.BEGINNING_OF_MONTH, next.getRecurrence());
        assertNull(next.getRecurrenceEndDate());
    }

    @Test
    void shouldPreserveRecurrenceEndDateOnNextRecurrence() {
        Instant endDate = Instant.parse("2026-12-01T00:00:00Z");
        scheduledTransfer.setRecurrenceEndDate(endDate);

        transactionScheduledService.scheduleNextRecurrence(scheduledTransfer);

        ArgumentCaptor<ScheduledTransfer> captor = ArgumentCaptor.forClass(ScheduledTransfer.class);
        verify(scheduledTransferRepository).save(captor.capture());

        assertEquals(endDate, captor.getValue().getRecurrenceEndDate());
    }
}
