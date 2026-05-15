package com.example.back.services;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.ScheduledTransfer;
import com.example.back.entities.transactions.Transaction;
import com.example.back.enums.RecurrenceType;
import com.example.back.enums.ScheduledTransactionType;
import com.example.back.repositories.ScheduledTransferRepository;

@Service
public class TransactionScheduledService {

    private final ScheduledTransferRepository scheduledTransferRepository;

    public TransactionScheduledService(ScheduledTransferRepository scheduledTransferRepository) {
        this.scheduledTransferRepository = scheduledTransferRepository;
    }

    public void createScheduledTransfer(Account accountOrigin, Account accountDestination,
            Double amount, String concept, Instant scheduledAt, String targetTimezone,
            RecurrenceType recurrence, Instant recurrenceEndDate) {
        scheduledTransferRepository.save(
                new ScheduledTransfer(accountOrigin, accountDestination, amount, concept,
                        scheduledAt, targetTimezone, recurrence, recurrenceEndDate));
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

    public boolean hasNextRecurrence(ScheduledTransfer scheduled) {
        if (scheduled.getRecurrence() == null)
            return false;
        if (scheduled.getRecurrenceEndDate() == null)
            return true;
        Instant next = calculateNextExecution(scheduled);
        return next.isBefore(scheduled.getRecurrenceEndDate());
    }

    public Instant calculateNextExecution(ScheduledTransfer scheduled) {
        ZonedDateTime current = scheduled.getScheduledAt()
                .atZone(ZoneId.of(scheduled.getTargetTimezone()));

        return switch (scheduled.getRecurrence()) {
            case BEGINNING_OF_MONTH ->
                current.plusMonths(1)
                        .withDayOfMonth(1)
                        .toInstant();

            case MIDDLE_OF_MONTH ->
                current.plusMonths(1)
                        .withDayOfMonth(15)
                        .toInstant();

            case END_OF_MONTH ->
                current.plusMonths(1)
                        .with(TemporalAdjusters.lastDayOfMonth())
                        .toInstant();
        };
    }

    public void scheduleNextRecurrence(ScheduledTransfer executed) {
        Instant nextExecution = calculateNextExecution(executed);
        scheduledTransferRepository.save(new ScheduledTransfer(
                executed.getAccountOrigin(),
                executed.getAccountDestination(),
                executed.getAmount(),
                executed.getConcept(),
                nextExecution,
                executed.getTargetTimezone(),
                executed.getRecurrence(),
                executed.getRecurrenceEndDate()));
    }
}
