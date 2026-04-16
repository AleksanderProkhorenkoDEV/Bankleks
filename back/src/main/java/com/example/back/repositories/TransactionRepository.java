package com.example.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back.entities.transaction.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {}
