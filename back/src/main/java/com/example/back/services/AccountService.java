package com.example.back.services;

import java.util.Random;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.back.dto.transaction.account.CreateAccountRequestDTO;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.user.User;
import com.example.back.repositories.AccountRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private UserService userService;

    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;

    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }


    /**
     * Cuenta el total de cuentas, genera un número aleatorio entre 0 y la cantidad de registros -1
     * el randomOffset, es la elección aleatoria de paginas que hay en la BBDD y obtiene un único 
     * elemento.
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
    public Account createAccount(CreateAccountRequestDTO request) {
        User user = userService.getUser(request.getUserId());
        Account account = new Account(request.getBalance(), user, request.getAccountNumber());
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

}
