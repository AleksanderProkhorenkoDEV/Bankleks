package com.example.back.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.ScheduledTransfer;
import com.example.back.enums.ScheduledTransactionType;

@Repository
public interface ScheduledTransferRepository extends JpaRepository<ScheduledTransfer, Long> {

        List<ScheduledTransfer> findByStatusAndScheduledAtBefore(
                        ScheduledTransactionType status, Instant now);

        List<ScheduledTransfer> findByAccountOriginAndStatus(
                        Account account, ScheduledTransactionType status);

        List<ScheduledTransfer> findByAccountOrigin(Account account);

        List<ScheduledTransfer> findByStatusAndCreatedAtBefore(
                        ScheduledTransactionType status, Instant threshold);

        @Query("SELECT s FROM ScheduledTransfer s WHERE s.status = 'FAILED'")
        Page<ScheduledTransfer> findAllFailed(Pageable pageable);
}
