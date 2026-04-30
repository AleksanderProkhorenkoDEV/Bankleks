package com.example.back.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.back.entities.transactions.Transaction;
import com.example.back.entities.user.User;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT DISTINCT t FROM Transaction t " +
            "LEFT JOIN t.accountDestination ad " +
            "WHERE t.user = :user " +
            "OR ad.user = :user")
    Page<Transaction> findAllByUserInvolved(@Param("user") User user, Pageable pageable);

    @Modifying
    @Query("UPDATE Transaction t SET t.user = null WHERE t.user.id = :userId")
    void nullifyUserReferences(@Param("userId") Long userId);

    Optional<Transaction> findById(Long id);
}
