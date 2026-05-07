package com.example.back.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.back.dto.transaction.account.AccountStatsDTO;
import com.example.back.entities.auth.Role;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.Transaction;
import com.example.back.entities.user.User;
import com.example.back.enums.TransactionType;
import com.example.back.repositories.AccountRepository;
import com.example.back.repositories.TransactionRepository;


@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AccountService accountService;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        user = new User("test", "test@gmail.com", "123456", new Role());
        user.setId(1L);
        account = new Account(1000.0, user, "ES1234567890");
        account.setId(1L);
    }

    @Test
    void shouldReturnZeroStatsWhenNoTransactions() {
        when(userService.getUser("test@gmail.com")).thenReturn(user);
        when(accountRepository.findByUserId(1L)).thenReturn(java.util.Optional.of(account));
        when(transactionRepository.findAllByUserInvolvedNoPagination(user)).thenReturn(List.of());

        AccountStatsDTO stats = accountService.getStats("test@gmail.com");

        assertEquals(0.0, stats.getTotalIncome());
        assertEquals(0.0, stats.getTotalExpense());
        assertNotNull(stats.getBalancePointDTO());
        assertEquals(1, stats.getBalancePointDTO().size()); // solo el punto actual
    }

    @Test
    void shouldCalculateTotalIncomeFromDeposits() {
        Transaction deposit1 = new Transaction("dep1", 200.0, LocalDate.now().minusDays(2),
                TransactionType.DEPOSIT, user, account, null);
        Transaction deposit2 = new Transaction("dep2", 300.0, LocalDate.now().minusDays(1),
                TransactionType.DEPOSIT, user, account, null);

        when(userService.getUser("test@gmail.com")).thenReturn(user);
        when(accountRepository.findByUserId(1L)).thenReturn(java.util.Optional.of(account));
        when(transactionRepository.findAllByUserInvolvedNoPagination(user))
                .thenReturn(List.of(deposit1, deposit2));

        AccountStatsDTO stats = accountService.getStats("test@gmail.com");

        assertEquals(500.0, stats.getTotalIncome());
        assertEquals(0.0, stats.getTotalExpense());
    }

    @Test
    void shouldCalculateTotalExpenseFromWithdrawals() {
        Transaction withdrawal = new Transaction("ret1", 150.0, LocalDate.now().minusDays(1),
                TransactionType.WITHDRAWAL, user, null, account);

        when(userService.getUser("test@gmail.com")).thenReturn(user);
        when(accountRepository.findByUserId(1L)).thenReturn(java.util.Optional.of(account));
        when(transactionRepository.findAllByUserInvolvedNoPagination(user))
                .thenReturn(List.of(withdrawal));

        AccountStatsDTO stats = accountService.getStats("test@gmail.com");

        assertEquals(0.0, stats.getTotalIncome());
        assertEquals(150.0, stats.getTotalExpense());
    }

    @Test
    void shouldCalculateIncomeFromIncomingTransfer() {
        User otherUser = new User("other", "other@gmail.com", "123456", new Role());
        Account otherAccount = new Account(500.0, otherUser, "ES9876543210");

        // transfer donde el destino es nuestro usuario → ingreso
        Transaction transfer = new Transaction("transf", 100.0, LocalDate.now().minusDays(1),
                TransactionType.TRANSFER, otherUser, account, otherAccount);

        when(userService.getUser("test@gmail.com")).thenReturn(user);
        when(accountRepository.findByUserId(1L)).thenReturn(java.util.Optional.of(account));
        when(transactionRepository.findAllByUserInvolvedNoPagination(user))
                .thenReturn(List.of(transfer));

        AccountStatsDTO stats = accountService.getStats("test@gmail.com");

        assertEquals(100.0, stats.getTotalIncome());
        assertEquals(0.0, stats.getTotalExpense());
    }

    @Test
    void shouldCalculateExpenseFromOutgoingTransfer() {
        User otherUser = new User("other", "other@gmail.com", "123456", new Role());
        Account otherAccount = new Account(500.0, otherUser, "ES9876543210");

        // transfer donde el origen es nuestro usuario → gasto
        Transaction transfer = new Transaction("transf", 100.0, LocalDate.now().minusDays(1),
                TransactionType.TRANSFER, user, otherAccount, account);

        when(userService.getUser("test@gmail.com")).thenReturn(user);
        when(accountRepository.findByUserId(1L)).thenReturn(java.util.Optional.of(account));
        when(transactionRepository.findAllByUserInvolvedNoPagination(user))
                .thenReturn(List.of(transfer));

        AccountStatsDTO stats = accountService.getStats("test@gmail.com");

        assertEquals(0.0, stats.getTotalIncome());
        assertEquals(100.0, stats.getTotalExpense());
    }

    @Test
    void shouldBuildBalanceEvolutionWithCorrectPointCount() {
        Transaction t1 = new Transaction("t1", 100.0, LocalDate.now().minusDays(2),
                TransactionType.DEPOSIT, user, account, null);
        Transaction t2 = new Transaction("t2", 50.0, LocalDate.now().minusDays(1),
                TransactionType.WITHDRAWAL, user, null, account);

        when(userService.getUser("test@gmail.com")).thenReturn(user);
        when(accountRepository.findByUserId(1L)).thenReturn(java.util.Optional.of(account));
        when(transactionRepository.findAllByUserInvolvedNoPagination(user))
                .thenReturn(List.of(t1, t2));

        AccountStatsDTO stats = accountService.getStats("test@gmail.com");

        // 2 transacciones + 1 punto actual = 3 puntos
        assertEquals(3, stats.getBalancePointDTO().size());
    }

    @Test
    void shouldSetLastBalancePointToCurrentBalance() {
        Transaction deposit = new Transaction("dep", 200.0, LocalDate.now().minusDays(1),
                TransactionType.DEPOSIT, user, account, null);

        when(userService.getUser("test@gmail.com")).thenReturn(user);
        when(accountRepository.findByUserId(1L)).thenReturn(java.util.Optional.of(account));
        when(transactionRepository.findAllByUserInvolvedNoPagination(user))
                .thenReturn(List.of(deposit));

        AccountStatsDTO stats = accountService.getStats("test@gmail.com");

        var points = stats.getBalancePointDTO();
        // el último punto debe ser el balance actual y la fecha de hoy
        assertEquals(account.getBalance(), points.get(points.size() - 1).getBalance());
        assertEquals(LocalDate.now(), points.get(points.size() - 1).getDate());
    }
}
