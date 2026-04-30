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

    public Page<Transaction> getAllTransaction(String email, Integer page, Integer sizePerPage) {
        User user = userService.getUser(email);
        Pageable pageable = PageRequest.of(page, sizePerPage);
        return transactionRepository.findAllByUserInvolved(user, pageable);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void createTransaction(CreateTransactionRequestDTO request, String email) {
        User user = userService.getUser(email);

        Account destinationAccount = request.getDestinationIban() != null
                ? accountService.getAccountByIban(request.getDestinationIban())
                : null;
        Account originAccount = request.getOriginIban() != null
                ? accountService.getAccountByIban(request.getOriginIban())
                : null;

        switch (request.getTransactionType()) {
            case DEPOSIT -> depositTransaction(request, user, destinationAccount);
            case WITHDRAWAL -> withdrawalTransaction(request, user, originAccount);
            case TRANSFER -> transferTransaction(request, user, destinationAccount, originAccount);
        }
    }

    private void depositTransaction(CreateTransactionRequestDTO request, User user, Account destination) {
        if (destination == null)
            throw new IllegalArgumentException("La cuenta destino no puede ser nula.");

        accountService.addBalance(destination, request.getAmount());
        transactionRepository.save(new Transaction(
                request.getConcept(), request.getAmount(), LocalDate.now(),
                TransactionType.DEPOSIT, user, destination, null));
    }

    private void withdrawalTransaction(CreateTransactionRequestDTO request, User user, Account origin) {
        if (origin == null)
            throw new IllegalArgumentException("La cuenta origen no puede ser nula.");

        accountService.subtractBalance(origin, request.getAmount());
        transactionRepository.save(new Transaction(
                request.getConcept(), request.getAmount(), LocalDate.now(),
                TransactionType.WITHDRAWAL, user, null, origin));
    }

    private void transferTransaction(CreateTransactionRequestDTO request, User user, Account destination,
            Account origin) {
        if (origin == null || destination == null)
            throw new IllegalArgumentException("La cuenta destino u origen no pueden ser nulas.");

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
