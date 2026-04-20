package com.example.back.services;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.back.dto.transaction.transaction.CreateTransactionRequestDTO;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.Transaction;
import com.example.back.entities.user.User;
import com.example.back.repositories.TransactionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class TransactionServices {

    private TransactionRepository transactionRepository;
    private AccountService accountService;
    private UserService userService;

    public TransactionServices(TransactionRepository transactionRepository, AccountService accountService,
            UserService userService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.userService = userService;
    }

    public Page<Transaction> getAllTransaction(Long id, Integer page, Integer sizePerPage) {
        Pageable pageable = PageRequest.of(page, sizePerPage);
        return transactionRepository.findAllByUserId(id, pageable);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Transaction createTransaction(CreateTransactionRequestDTO request) {
        User user = userService.getUser(request.getUserId());
        Account destinatiAccount = accountService.getAccount(request.getDestinationAccountId());
        Account originAccount = accountService.getAccount(request.getOriginAccountId());
        return transactionRepository.save(
                new Transaction(
                        request.getConcept(),
                        request.getAmount(),
                        LocalDate.now(),
                        request.getTransactionType(),
                        user,
                        destinatiAccount,
                        originAccount));
    }
}
