package com.example.back.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back.entities.transactions.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findById(Long id);

}
