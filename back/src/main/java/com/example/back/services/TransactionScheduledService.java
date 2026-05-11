package com.example.back.services;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.ScheduledTransfer;
import com.example.back.entities.transactions.Transaction;
import com.example.back.enums.ScheduledTransactionType;
import com.example.back.repositories.ScheduledTransferRepository;

@Service
public class TransactionScheduledService {

    private ScheduledTransferRepository scheduledTransferRepository;

    public TransactionScheduledService(ScheduledTransferRepository scheduledTransferRepository) {
        this.scheduledTransferRepository = scheduledTransferRepository;
    }

    public void createScheduledTransfer(Account accountOrigin, Account accountDestination,
            Double amount, String concept, Instant scheduledAt, String targetTimezone) {
        scheduledTransferRepository.save(
                new ScheduledTransfer(accountOrigin, accountDestination, amount, concept, scheduledAt, targetTimezone));
    }

    public List<ScheduledTransfer> getPendingTransfers() {
        return scheduledTransferRepository.findByStatusAndScheduledAtBefore(
                ScheduledTransactionType.SCHEDULED, Instant.now());
    }

    public void markAsExecuting(ScheduledTransfer scheduled) {
        scheduled.setStatus(ScheduledTransactionType.EXECUTING);
        scheduledTransferRepository.save(scheduled);
    }

    public void markAsExecuted(ScheduledTransfer scheduled, Transaction transaction) {
        scheduled.setTransaction(transaction);
        scheduled.setStatus(ScheduledTransactionType.EXECUTED);
        scheduledTransferRepository.save(scheduled);
    }

    public void markAsFailed(ScheduledTransfer scheduled) {
        scheduled.setStatus(ScheduledTransactionType.FAILED);
        scheduledTransferRepository.save(scheduled);
    }
}
