package com.example.back.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.back.entities.transactions.ScheduledTransfer;
import com.example.back.entities.transactions.Transaction;
import com.example.back.services.AccountService;
import com.example.back.services.TransactionScheduledService;
import com.example.back.services.TransactionServices;

import jakarta.transaction.Transactional;

@Component
public class ScheduledTransferCron {

    private final TransactionScheduledService transactionScheduledService;
    private final AccountService accountService;
    private final TransactionServices transactionServices;

    public ScheduledTransferCron(TransactionScheduledService transactionScheduledService, AccountService accountService, TransactionServices transactionServices) {
        this.transactionScheduledService = transactionScheduledService;
        this.accountService = accountService;
        this.transactionServices = transactionServices;
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void processScheduledTransfers() {
        List<ScheduledTransfer> pending = transactionScheduledService.getPendingTransfers();
        for (ScheduledTransfer scheduled : pending) {
            processOne(scheduled);
        }
    }

    private void processOne(ScheduledTransfer scheduled) {
        try {
            transactionScheduledService.markAsExecuting(scheduled);

            accountService.subtractBalance(scheduled.getAccountOrigin(), scheduled.getAmount());
            accountService.releaseReservedBalance(scheduled.getAccountOrigin(), scheduled.getAmount());
            accountService.addBalance(scheduled.getAccountDestination(), scheduled.getAmount());

            Transaction transaction = transactionServices.createTransferTransaction(
                    scheduled.getConcept(),
                    scheduled.getAmount(),
                    scheduled.getAccountOrigin(),
                    scheduled.getAccountDestination());

            transactionScheduledService.markAsExecuted(scheduled, transaction);

        } catch (Exception e) {
            accountService.releaseReservedBalance(scheduled.getAccountOrigin(), scheduled.getAmount());
            transactionScheduledService.markAsFailed(scheduled);
        }
    }
}
