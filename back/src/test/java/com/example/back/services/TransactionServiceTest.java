package com.example.back.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.example.back.dto.transaction.transaction.CreateTransactionRequestDTO;
import com.example.back.dto.transaction.transaction.UpdateConceptRequestDTO;
import com.example.back.entities.auth.Role;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.Transaction;
import com.example.back.entities.user.User;
import com.example.back.enums.TransactionType;
import com.example.back.repositories.TransactionRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @InjectMocks
    private TransactionServices transactionServices;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private UserService userService;

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
    }

    // --- getAllTransaction ---

    @Test
    void shouldReturnPageOfTransactions() {
        Page<Transaction> page = new PageImpl<>(List.of());

        when(userService.getUser(user.getEmail())).thenReturn(user);
        when(transactionRepository.findAllByUserInvolved(user, PageRequest.of(0, 25)))
                .thenReturn(page);

        Page<Transaction> result = transactionServices.getAllTransaction(user.getEmail(), 0, 25);

        assertEquals(page, result);
    }

    @Test
    void shouldReturnTransactionById() {
        Transaction transaction = new Transaction("concept", 10.0, Instant.now(),
                TransactionType.DEPOSIT, user, destination, null, null);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        Transaction result = transactionServices.getTransactionById(1L);

        assertEquals(transaction, result);
    }

    @Test
    void shouldThrowIfTransactionNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionServices.getTransactionById(99L));
    }

    @Test
    void shouldCreateDepositTransaction() {
        CreateTransactionRequestDTO request = new CreateTransactionRequestDTO(
                "deposit", 50.0, destination.getAccountNumber(), null, TransactionType.DEPOSIT);

        when(userService.getUser(user.getEmail())).thenReturn(user);
        when(accountService.getAccountByIban(destination.getAccountNumber())).thenReturn(destination);

        transactionServices.createTransaction(request, user.getEmail());

        verify(accountService).addBalance(destination, 50.0);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldThrowIfDestinationIsNullOnDeposit() {
        CreateTransactionRequestDTO request = new CreateTransactionRequestDTO(
                "deposit", 50.0, null, null, TransactionType.DEPOSIT);

        when(userService.getUser(user.getEmail())).thenReturn(user);

        assertThrows(IllegalArgumentException.class,
                () -> transactionServices.createTransaction(request, user.getEmail()));
    }

    @Test
    void shouldCreateWithdrawalTransaction() {
        CreateTransactionRequestDTO request = new CreateTransactionRequestDTO(
                "withdrawal", 50.0, null, origin.getAccountNumber(), TransactionType.WITHDRAWAL);

        when(userService.getUser(user.getEmail())).thenReturn(user);
        when(accountService.getAccountByIban(origin.getAccountNumber())).thenReturn(origin);

        transactionServices.createTransaction(request, user.getEmail());

        verify(accountService).subtractBalance(origin, 50.0);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldThrowIfOriginIsNullOnWithdrawal() {
        CreateTransactionRequestDTO request = new CreateTransactionRequestDTO(
                "withdrawal", 50.0, null, null, TransactionType.WITHDRAWAL);

        when(userService.getUser(user.getEmail())).thenReturn(user);

        assertThrows(IllegalArgumentException.class,
                () -> transactionServices.createTransaction(request, user.getEmail()));
    }

    @Test
    void shouldCreateTransferTransaction() {
        CreateTransactionRequestDTO request = new CreateTransactionRequestDTO(
                "transfer", 50.0, destination.getAccountNumber(), origin.getAccountNumber(), TransactionType.TRANSFER);

        when(userService.getUser(user.getEmail())).thenReturn(user);
        when(accountService.getAccountByIban(destination.getAccountNumber())).thenReturn(destination);
        when(accountService.getAccountByIban(origin.getAccountNumber())).thenReturn(origin);

        transactionServices.createTransaction(request, user.getEmail());

        verify(accountService).subtractBalance(origin, 50.0);
        verify(accountService).addBalance(destination, 50.0);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldThrowIfOriginIsNullOnTransfer() {
        CreateTransactionRequestDTO request = new CreateTransactionRequestDTO(
                "transfer", 50.0, destination.getAccountNumber(), null, TransactionType.TRANSFER);

        when(userService.getUser(user.getEmail())).thenReturn(user);
        when(accountService.getAccountByIban(destination.getAccountNumber())).thenReturn(destination);

        assertThrows(IllegalArgumentException.class,
                () -> transactionServices.createTransaction(request, user.getEmail()));
    }

    @Test
    void shouldThrowIfDestinationIsNullOnTransfer() {
        CreateTransactionRequestDTO request = new CreateTransactionRequestDTO(
                "transfer", 50.0, null, origin.getAccountNumber(), TransactionType.TRANSFER);

        when(userService.getUser(user.getEmail())).thenReturn(user);
        when(accountService.getAccountByIban(origin.getAccountNumber())).thenReturn(origin);

        assertThrows(IllegalArgumentException.class,
                () -> transactionServices.createTransaction(request, user.getEmail()));
    }

    @Test
    void shouldUpdateConcept() {
        Transaction transaction = new Transaction("old concept", 10.0, Instant.now(),
                TransactionType.DEPOSIT, user, destination, null, null);
        UpdateConceptRequestDTO request = new UpdateConceptRequestDTO("new concept", 1L);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        transactionServices.updateConcept(request);

        assertEquals("new concept", transaction.getConcept());
    }

    @Test
    void shouldThrowIfTransactionNotFoundOnUpdateConcept() {
        UpdateConceptRequestDTO request = new UpdateConceptRequestDTO("new concept", 99L);

        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionServices.updateConcept(request));
    }
}
