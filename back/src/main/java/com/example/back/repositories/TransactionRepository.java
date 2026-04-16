package com.example.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.back.entities.transaction.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {}
