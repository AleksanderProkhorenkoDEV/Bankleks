package com.example.back.services;

import org.springframework.stereotype.Service;

import com.example.back.dto.transaction.account.CreateAccountRequestDTO;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.user.User;
import com.example.back.repositories.AccountRepository;
import com.example.back.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Account createAccount(CreateAccountRequestDTO request) {
        User user = getUser(request.getUserId());
        Account account = new Account(request.getBalance(), user, request.getAccountNumber());
        return accountRepository.save(account);
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

}
