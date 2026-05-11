package com.example.back.services;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.ScheduledTransfer;
import com.example.back.repositories.ScheduledTransferRepository;

@Service
public class TransactionScheduledService {

    private ScheduledTransferRepository scheduledTransferRepository;

    public TransactionScheduledService(ScheduledTransferRepository scheduledTransferRepository) {
        this.scheduledTransferRepository = scheduledTransferRepository;
    }

    public void ScheduledTransfer(Account accountOrigin, Account accountDestination,
            Double amount, String concept, Instant scheduledAt, String targetTimezone) {
        scheduledTransferRepository.save(
                new ScheduledTransfer(accountOrigin, accountDestination, amount, concept, scheduledAt, targetTimezone));
    }
}
