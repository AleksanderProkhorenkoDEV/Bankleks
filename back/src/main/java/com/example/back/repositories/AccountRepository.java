package com.example.back.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.back.entities.transactions.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {}
