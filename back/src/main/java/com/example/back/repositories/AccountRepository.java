package com.example.back.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back.entities.transactions.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {}
