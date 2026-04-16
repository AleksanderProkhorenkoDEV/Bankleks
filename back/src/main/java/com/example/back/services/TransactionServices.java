package com.example.back.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.back.dto.transaction.transaction.CreateTransactionRequestDTO;
import com.example.back.entities.transactions.Transaction;
import com.example.back.entities.user.User;
import com.example.back.repositories.TransactionRepository;
import com.example.back.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TransactionServices {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;

    public TransactionServices(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Page<Transaction> getAllTransaction(Long id, Integer page, Integer sizePerPage) {
        Pageable pageable = PageRequest.of(page, sizePerPage);
        return transactionRepository.findAllByUserId(id, pageable);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    // public Transaction createTransaction(CreateTransactionRequestDTO request){
    //     User user = getUser(request.getUserId());
    //     return transactionRepository.save(new Transaction());
    // }

    // private User getUser(Long id){
    //     return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    // }
}
