package com.example.back.scheduled;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.back.entities.auth.Role;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.ScheduledTransfer;
import com.example.back.entities.transactions.Transaction;
import com.example.back.entities.user.User;
import com.example.back.enums.RecurrenceType;
import com.example.back.scheduler.ScheduledTransferCron;
import com.example.back.services.AccountService;
import com.example.back.services.TransactionScheduledService;
import com.example.back.services.TransactionServices;

@ExtendWith(MockitoExtension.class)
public class ScheduledTransferCronTest {

    @InjectMocks
    private ScheduledTransferCron scheduledTransferCron;

    @Mock
    private TransactionScheduledService transactionScheduledService;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionServices transactionServices;

    private User user;
    private Account origin;
    private Account destination;
    private ScheduledTransfer scheduledTransfer;

    @BeforeEach
    void setUp() {
        user = new User("test", "test@gmail.com", "123456789", new Role());
        user.setId(1L);
        origin = new Account(500.0, "9876543211234567899516", "UTC", user);
        origin.setId(1L);
        origin.setBalanceReserved(100.0);
        destination = new Account(200.0, "1234567899876543211234", "UTC", user);
        destination.setId(2L);

        scheduledTransfer = new ScheduledTransfer(origin, destination, 100.0, "pago mensual",
                Instant.now(), "UTC", RecurrenceType.BEGINNING_OF_MONTH, null, null, null);
        scheduledTransfer.setId(1L);
    }

    // ─────────────────────────────────────────────
    // processScheduledTransfers — flujo general
    // ─────────────────────────────────────────────

    @Test
    void shouldProcessNothingWhenNoPendingTransfers() {
        when(transactionScheduledService.getPendingTransfers()).thenReturn(List.of());

        scheduledTransferCron.processScheduledTransfers();

        verify(transactionScheduledService).getPendingTransfers();
        verifyNoInteractions(accountService, transactionServices);
    }

    @Test
    void shouldProcessAllPendingTransfers() {
        ScheduledTransfer transfer2 = new ScheduledTransfer(origin, destination, 50.0, "otro pago",
                Instant.now(), "UTC", null, null, null, null);
        transfer2.setId(2L);

        Transaction tx1 = mock(Transaction.class);
        Transaction tx2 = mock(Transaction.class);

        when(transactionScheduledService.getPendingTransfers()).thenReturn(List.of(scheduledTransfer, transfer2));
        when(transactionServices.createTransferTransaction(eq("pago mensual"), eq(100.0), eq(origin), eq(destination),
                eq(scheduledTransfer))).thenReturn(tx1);
        when(transactionServices.createTransferTransaction(eq("otro pago"), eq(50.0), eq(origin), eq(destination),
                eq(transfer2))).thenReturn(tx2);

        scheduledTransferCron.processScheduledTransfers();

        verify(transactionScheduledService).markAsExecuted(scheduledTransfer, tx1);
        verify(transactionScheduledService).markAsExecuted(transfer2, tx2);
    }

    @Test
    void shouldExecuteTransferInCorrectOrder() {
        Transaction transaction = mock(Transaction.class);

        when(transactionScheduledService.getPendingTransfers()).thenReturn(List.of(scheduledTransfer));
        when(transactionServices.createTransferTransaction(any(), any(), any(), any(), any()))
                .thenReturn(transaction);

        scheduledTransferCron.processScheduledTransfers();

        InOrder order = inOrder(transactionScheduledService, accountService, transactionServices);
        order.verify(transactionScheduledService).markAsExecuting(scheduledTransfer);
        order.verify(accountService).subtractBalance(origin, 100.0);
        order.verify(accountService).releaseReservedBalance(origin, 100.0);
        order.verify(accountService).addBalance(destination, 100.0);
        order.verify(transactionServices).createTransferTransaction(
                "pago mensual", 100.0, origin, destination, scheduledTransfer);
        order.verify(transactionScheduledService).markAsExecuted(scheduledTransfer, transaction);
    }

    @Test
    void shouldMarkTransferAsExecutedAfterSuccess() {
        Transaction transaction = mock(Transaction.class);

        when(transactionScheduledService.getPendingTransfers()).thenReturn(List.of(scheduledTransfer));
        when(transactionServices.createTransferTransaction(any(), any(), any(), any(), any()))
                .thenReturn(transaction);

        scheduledTransferCron.processScheduledTransfers();

        verify(transactionScheduledService).markAsExecuted(scheduledTransfer, transaction);
        verify(transactionScheduledService, never()).markAsFailed(any());
    }

    @Test
    void shouldMarkAsFailedWhenSubtractBalanceThrows() {
        when(transactionScheduledService.getPendingTransfers()).thenReturn(List.of(scheduledTransfer));
        doThrow(new IllegalArgumentException("Saldo insuficiente"))
                .when(accountService).subtractBalance(origin, 100.0);

        scheduledTransferCron.processScheduledTransfers();

        verify(accountService).releaseReservedBalance(origin, 100.0);
        verify(transactionScheduledService).markAsFailed(scheduledTransfer);
        verify(transactionScheduledService, never()).markAsExecuted(any(), any());
    }

    @Test
    void shouldMarkAsFailedWhenCreateTransactionThrows() {
        when(transactionScheduledService.getPendingTransfers()).thenReturn(List.of(scheduledTransfer));
        when(transactionServices.createTransferTransaction(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("DB error"));

        scheduledTransferCron.processScheduledTransfers();

        verify(accountService, times(2)).releaseReservedBalance(origin, 100.0);
        verify(transactionScheduledService).markAsFailed(scheduledTransfer);
        verify(transactionScheduledService, never()).markAsExecuted(any(), any());
    }

    @Test
    void shouldReleaseReservedBalanceOnFailure() {
        when(transactionScheduledService.getPendingTransfers()).thenReturn(List.of(scheduledTransfer));
        doThrow(new RuntimeException("Error inesperado"))
                .when(accountService).addBalance(destination, 100.0);

        scheduledTransferCron.processScheduledTransfers();

        verify(accountService, times(2)).releaseReservedBalance(origin, 100.0);
        verify(transactionScheduledService).markAsFailed(scheduledTransfer);
    }

    @Test
    void shouldContinueProcessingOtherTransfersAfterOneFailure() {
        ScheduledTransfer transfer2 = new ScheduledTransfer(origin, destination, 50.0, "pago 2",
                Instant.now(), "UTC", null, null, null, null);
        transfer2.setId(2L);

        Transaction tx2 = mock(Transaction.class);

        when(transactionScheduledService.getPendingTransfers()).thenReturn(List.of(scheduledTransfer, transfer2));
        doThrow(new RuntimeException("fallo"))
                .when(accountService).subtractBalance(origin, 100.0);
        when(transactionServices.createTransferTransaction(eq("pago 2"), eq(50.0), eq(origin), eq(destination),
                eq(transfer2))).thenReturn(tx2);

        // El segundo subtractBalance (50.0) no lanza
        doNothing().when(accountService).subtractBalance(origin, 50.0);

        scheduledTransferCron.processScheduledTransfers();

        verify(transactionScheduledService).markAsFailed(scheduledTransfer);
        verify(transactionScheduledService).markAsExecuted(transfer2, tx2);
    }
}