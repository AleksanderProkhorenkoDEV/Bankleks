package com.example.back.services;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.back.dto.transaction.transaction.CreateTransactionRequestDTO;
import com.example.back.dto.transaction.transaction.UpdateConceptRequestDTO;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.Transaction;
import com.example.back.entities.user.User;
import com.example.back.enums.TransactionType;
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
    public void createTransaction(CreateTransactionRequestDTO request) {
        User user = userService.getUser(request.getUserId());
        Account destinationAccount = request.getDestinationAccountId() != null
                ? accountService.getAccount(request.getDestinationAccountId())
                : null;
        Account originAccount = request.getOriginAccountId() != null
                ? accountService.getAccount(request.getOriginAccountId())
                : null;

        switch (request.getTransactionType()) {
            case DEPOSIT -> depositTransaction(request, user, destinationAccount);
            case WITHDRAWAL -> withdrawalTransaction(request, user, originAccount);
            case TRANSFER -> transferTransaction(request, user, destinationAccount, originAccount);
        }
    }

    private void depositTransaction(CreateTransactionRequestDTO request, User user, Account destination) {
        accountService.addBalance(destination, request.getAmount());

        transactionRepository.save(new Transaction(
                request.getConcept(), request.getAmount(), LocalDate.now(),
                TransactionType.DEPOSIT, user, destination, null));
    }

    private void withdrawalTransaction(CreateTransactionRequestDTO request, User user, Account origin) {
        accountService.subtractBalance(origin, request.getAmount());

        transactionRepository.save(new Transaction(
                request.getConcept(), request.getAmount(), LocalDate.now(),
                TransactionType.WITHDRAWAL, user, null, origin));
    }

    private void transferTransaction(CreateTransactionRequestDTO request, User user, Account destination,
            Account origin) {
        accountService.subtractBalance(origin, request.getAmount());
        accountService.addBalance(destination, request.getAmount());

        transactionRepository.save(new Transaction(
                request.getConcept(), request.getAmount(), LocalDate.now(),
                TransactionType.TRANSFER, user, destination, origin));
    }

    @Transactional
    public void updateConcept(UpdateConceptRequestDTO request) {
        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(EntityNotFoundException::new);

        transaction.setConcept(request.getConcept());
    }
}
