package com.example.back.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.back.dto.transaction.transaction.CreateTransactionScheduledRequestDTO;
import com.example.back.entities.auth.Role;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.ScheduledTransfer;
import com.example.back.entities.transactions.Transaction;
import com.example.back.entities.user.User;
import com.example.back.enums.RecurrenceType;
import com.example.back.enums.TransactionType;
import com.example.back.repositories.TransactionRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class TransactionServicesTest {

        @InjectMocks
        private TransactionServices transactionServices;

        @Mock
        private TransactionRepository transactionRepository;

        @Mock
        private AccountService accountService;

        @Mock
        private UserService userService;

        @Mock
        private TransactionScheduledService transactionScheduledService;

        private User user;
        private Account destination;
        private Account origin;

        @BeforeEach
        void setUp() {
                user = new User("test", "test@gmail.com", "123456789", new Role());
                user.setId(1L);
                destination = new Account(150.50, "1234567899876543211234", "UTC", user);
                destination.setId(1L);
                origin = new Account(350.69, "9876543211234567899516", "UTC", user);
                origin.setId(2L);
                origin.setBalanceReserved(0.0);
        }

        @Test
        void shouldDeleteDepositAndSubtractFromDestination() {
                Transaction transaction = new Transaction("dep", 100.0, Instant.now(),
                                TransactionType.DEPOSIT, user, destination, null, null);
                transaction.setId(1L);

                when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

                transactionServices.deleteTransaction(1L);

                verify(accountService).subtractBalance(destination, 100.0);
                verify(transactionRepository).delete(transaction);
        }

        @Test
        void shouldDeleteDepositWithNullDestinationWithoutCallingSubtract() {
                Transaction transaction = new Transaction("dep", 100.0, Instant.now(),
                                TransactionType.DEPOSIT, user, null, null, null);
                transaction.setId(1L);

                when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

                transactionServices.deleteTransaction(1L);

                verify(accountService, never()).subtractBalance(any(), any());
                verify(transactionRepository).delete(transaction);
        }

        @Test
        void shouldDeleteWithdrawalAndAddBalanceToOrigin() {
                Transaction transaction = new Transaction("withdrawal", 80.0, Instant.now(),
                                TransactionType.WITHDRAWAL, user, null, origin, null);
                transaction.setId(2L);

                when(transactionRepository.findById(2L)).thenReturn(Optional.of(transaction));

                transactionServices.deleteTransaction(2L);

                verify(accountService).addBalance(origin, 80.0);
                verify(transactionRepository).delete(transaction);
        }

        @Test
        void shouldDeleteWithdrawalWithNullOriginWithoutCallingAdd() {
                Transaction transaction = new Transaction("withdrawal", 80.0, Instant.now(),
                                TransactionType.WITHDRAWAL, user, null, null, null);
                transaction.setId(2L);

                when(transactionRepository.findById(2L)).thenReturn(Optional.of(transaction));

                transactionServices.deleteTransaction(2L);

                verify(accountService, never()).addBalance(any(), any());
                verify(transactionRepository).delete(transaction);
        }

        @Test
        void shouldDeleteTransferAndRevertBothAccounts() {
                Transaction transaction = new Transaction("transfer", 50.0, Instant.now(),
                                TransactionType.TRANSFER, user, destination, origin, null);
                transaction.setId(3L);

                when(transactionRepository.findById(3L)).thenReturn(Optional.of(transaction));

                transactionServices.deleteTransaction(3L);

                verify(accountService).addBalance(origin, 50.0);
                verify(accountService).subtractBalance(destination, 50.0);
                verify(transactionRepository).delete(transaction);
        }

        @Test
        void shouldThrowWhenDeletingNonExistentTransaction() {
                when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

                assertThrows(EntityNotFoundException.class, () -> transactionServices.deleteTransaction(99L));
        }

        @Test
        void shouldThrowWhenTimezoneIsInvalid() {
                CreateTransactionScheduledRequestDTO request = new CreateTransactionScheduledRequestDTO("pago", 50.0,
                                destination.getAccountNumber(), origin.getAccountNumber(), "Mars/Olympus", "10:00:00",
                                null, null, null, null);

                assertThrows(IllegalArgumentException.class,
                                () -> transactionServices.createScheduledTransaction(request, user.getEmail()));
        }

        @Test
        void shouldThrowWhenScheduledDateIsInThePast() {
                String pastDate = LocalDate.now().minusDays(1).toString();

                CreateTransactionScheduledRequestDTO request = new CreateTransactionScheduledRequestDTO("pago", 50.0,
                                destination.getAccountNumber(), origin.getAccountNumber(), "UTC", "10:00:00",
                                List.of(pastDate), null, null, null);

                when(accountService.getAccountByIban(origin.getAccountNumber())).thenReturn(origin);
                when(accountService.getAccountByIban(destination.getAccountNumber())).thenReturn(destination);

                assertThrows(IllegalArgumentException.class,
                                () -> transactionServices.createScheduledTransaction(request, user.getEmail()));
        }

        @Test
        void shouldThrowWhenBalanceIsInsufficient() {
                String futureDate = LocalDate.now().plusDays(1).toString();

                // 3 fechas × 200 = 600, pero el saldo es 350.69
                CreateTransactionScheduledRequestDTO request = new CreateTransactionScheduledRequestDTO("pago", 200.0,
                                destination.getAccountNumber(), origin.getAccountNumber(), "UTC", "10:00:00",
                                List.of(futureDate, futureDate, futureDate), null, null, null);

                when(accountService.getAccountByIban(origin.getAccountNumber())).thenReturn(origin);
                when(accountService.getAccountByIban(destination.getAccountNumber())).thenReturn(destination);

                assertThrows(IllegalArgumentException.class,
                                () -> transactionServices.createScheduledTransaction(request, user.getEmail()));
        }

        @Test
        void shouldCreateOneScheduledTransferPerDate() {
                String futureDate1 = LocalDate.now().plusDays(1).toString();
                String futureDate2 = LocalDate.now().plusDays(2).toString();

                CreateTransactionScheduledRequestDTO request = new CreateTransactionScheduledRequestDTO("pago", 50.0,
                                destination.getAccountNumber(), origin.getAccountNumber(), "UTC", "10:00:00",
                                List.of(futureDate1, futureDate2), null, null, null);

                when(accountService.getAccountByIban(origin.getAccountNumber())).thenReturn(origin);
                when(accountService.getAccountByIban(destination.getAccountNumber())).thenReturn(destination);

                transactionServices.createScheduledTransaction(request, user.getEmail());

                // Una llamada a addReservedBalance y createScheduledTransfer por cada fecha
                verify(accountService, times(2)).addReservedBalance(origin, 50.0);
                verify(transactionScheduledService, times(2)).createScheduledTransfer(
                                eq(origin), eq(destination), eq(50.0), eq("pago"),
                                any(Instant.class), eq("UTC"), isNull(), isNull(), isNull());
        }

        @Test
        void shouldCreateRecurrentScheduledTransfer() {
                String futureDate = LocalDate.now().plusDays(1).toString();

                CreateTransactionScheduledRequestDTO request = new CreateTransactionScheduledRequestDTO("suscripción",
                                50.0,
                                destination.getAccountNumber(), origin.getAccountNumber(), "UTC", "23:59:59",
                                List.of(futureDate),
                                RecurrenceType.END_OF_MONTH, LocalDateTime.now().plusMonths(3), null);

                when(accountService.getAccountByIban(origin.getAccountNumber())).thenReturn(origin);
                when(accountService.getAccountByIban(destination.getAccountNumber())).thenReturn(destination);

                transactionServices.createScheduledTransaction(request, user.getEmail());

                verify(accountService).addReservedBalance(eq(origin), eq(50.0));
                verify(transactionScheduledService).createScheduledTransfer(
                                eq(origin), eq(destination), eq(50.0), eq("suscripción"),
                                any(Instant.class), eq("UTC"), eq(RecurrenceType.END_OF_MONTH), any(Instant.class),
                                (Integer) null);
        }

        @Test
        void shouldCreateRecurrentTransferWithNullEndDate() {
                String futureDate = LocalDate.now().plusDays(1).toString();

                CreateTransactionScheduledRequestDTO request = new CreateTransactionScheduledRequestDTO(
                                "suscripción", 50.0,
                                destination.getAccountNumber(), origin.getAccountNumber(),
                                "UTC", "23:59:59", List.of(futureDate),
                                RecurrenceType.END_OF_MONTH, LocalDateTime.now().plusMonths(3),
                                (Integer) null);

                when(accountService.getAccountByIban(origin.getAccountNumber())).thenReturn(origin);
                when(accountService.getAccountByIban(destination.getAccountNumber())).thenReturn(destination);

                transactionServices.createScheduledTransaction(request, user.getEmail());

                verify(transactionScheduledService).createScheduledTransfer(
                                eq(origin), eq(destination), eq(50.0), eq("pago recurrente"),
                                any(Instant.class), eq("UTC"), eq(RecurrenceType.BEGINNING_OF_MONTH), isNull(), null);
        }

        @Test
        void shouldSaveAndReturnTransferTransaction() {
                ScheduledTransfer scheduledTransfer = mock(ScheduledTransfer.class);

                Transaction saved = new Transaction("pago auto", 75.0, Instant.now(),
                                TransactionType.TRANSFER, user, destination, origin, scheduledTransfer);

                when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

                Transaction result = transactionServices.createTransferTransaction(
                                "pago auto", 75.0, origin, destination, scheduledTransfer);

                assertNotNull(result);
                assertEquals(TransactionType.TRANSFER, result.getType());
                assertEquals(75.0, result.getAmount());

                ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
                verify(transactionRepository).save(captor.capture());

                Transaction persisted = captor.getValue();
                assertEquals(origin, persisted.getAccountOrigin());
                assertEquals(destination, persisted.getAccountDestination());
                assertEquals(user, persisted.getUser());
                assertEquals(scheduledTransfer, persisted.getScheduledTransfer());
        }

        @Test
        void shouldSetUserFromOriginAccountOnCreateTransfer() {
                User otherUser = new User("other", "other@gmail.com", "pass", new Role());
                Account otherOrigin = new Account(500.0, "1111111111111111111111", "UTC", otherUser);

                when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

                Transaction result = transactionServices.createTransferTransaction(
                                "concepto", 100.0, otherOrigin, destination, null);

                assertEquals(otherUser, result.getUser());
        }
}