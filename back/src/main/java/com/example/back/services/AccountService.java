package com.example.back.services;

import java.util.Random;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.back.entities.transactions.Account;
import com.example.back.entities.user.User;
import com.example.back.repositories.AccountRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;

    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
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

}
