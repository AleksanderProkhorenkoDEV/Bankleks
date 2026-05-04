package com.example.back.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.back.dto.transaction.account.AccountStatsDTO;
import com.example.back.dto.transaction.account.BalancePointDTO;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.Transaction;
import com.example.back.entities.user.User;
import com.example.back.enums.TransactionType;
import com.example.back.repositories.AccountRepository;
import com.example.back.repositories.TransactionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private UserService userService;
    private TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, UserService userService, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.transactionRepository = transactionRepository;

    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Account getAccountByIban(String iban) {
        return accountRepository.findByAccountNumber(iban)
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Cuenta el total de cuentas, genera un número aleatorio entre 0 y la cantidad
     * de registros -1
     * el randomOffset, es la elección aleatoria de paginas que hay en la BBDD y
     * obtiene un único
     * elemento.
     * 
     * @return Account
     */
    public Account getRandomAccount() {
        long count = accountRepository.count();
        int randomOffset = new Random().nextInt((int) count);
        return accountRepository.findAll(PageRequest.of(randomOffset, 1))
                .getContent()
                .get(0);
    }

    @Transactional
    public Account createAccount(User user) {
        Account account = new Account(0D, user, this.generarIbanSimple());
        return accountRepository.save(account);
    }

    public void addBalance(Account account, Double amount) {
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
    }

    public void subtractBalance(Account account, Double amount) {
        if (account.getBalance() < amount) {
            throw new RuntimeException("No dispone de fondos suficientes");
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
    }

    private String generarIbanSimple() {
        Random random = new Random();
        StringBuilder iban = new StringBuilder("ES");

        for (int i = 0; i < 22; i++) {
            iban.append(random.nextInt(10));
        }

        return iban.toString();
    }

    public AccountStatsDTO getStats(String email) {
        User user = userService.getUser(email);
        Account account = this.getAccount(user.getId());

        List<Transaction> transactions = transactionRepository
                .findAllByUserInvolvedNoPagination(user);

        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.DEPOSIT ||
                        (t.getType() == TransactionType.TRANSFER &&
                                t.getAccountDestination() != null &&
                                t.getAccountDestination().getUser().equals(user)))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.WITHDRAWAL ||
                        (t.getType() == TransactionType.TRANSFER &&
                                t.getAccountOrigin() != null &&
                                t.getAccountOrigin().getUser().equals(user)))
                .mapToDouble(Transaction::getAmount)
                .sum();

        List<BalancePointDTO> evolution = buildBalanceEvolution(transactions, account, user);

        return new AccountStatsDTO(totalIncome, totalExpense, evolution);
    }

    private List<BalancePointDTO> buildBalanceEvolution(List<Transaction> transactions, Account account, User user) {
        // ordenamos por fecha
        List<Transaction> sorted = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDay))
                .toList();

        // reconstruimos el balance hacia atrás desde el actual
        double currentBalance = account.getBalance();
        LinkedList<BalancePointDTO> points = new LinkedList<>();

        for (int i = sorted.size() - 1; i >= 0; i--) {
            Transaction t = sorted.get(i);
            points.addFirst(new BalancePointDTO(t.getTransactionDay(), currentBalance));

            boolean isIncome = t.getType() == TransactionType.DEPOSIT ||
                    (t.getType() == TransactionType.TRANSFER &&
                            t.getAccountDestination() != null &&
                            t.getAccountDestination().getUser().equals(user));

            if (isIncome)
                currentBalance -= t.getAmount();
            else
                currentBalance += t.getAmount();
        }

        // añadimos el punto actual al final
        points.add(new BalancePointDTO(LocalDate.now(), account.getBalance()));

        return points;
    }

}
