package com.example.back.services;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.ScheduledTransfer;
import com.example.back.entities.transactions.Transaction;
import com.example.back.enums.RecurrenceType;
import com.example.back.enums.ScheduledTransactionType;
import com.example.back.enums.TransactionType;
import com.example.back.repositories.ScheduledTransferRepository;
import com.example.back.repositories.TransactionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class TransactionScheduledService {

    private final ScheduledTransferRepository scheduledTransferRepository;
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;

    public TransactionScheduledService(ScheduledTransferRepository scheduledTransferRepository,
            AccountService accountService,
            TransactionRepository transactionRepository) {
        this.scheduledTransferRepository = scheduledTransferRepository;
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
    }

    public void createScheduledTransfer(Account accountOrigin, Account accountDestination,
            Double amount, String concept, Instant scheduledAt, String targetTimezone,
            RecurrenceType recurrence, Instant recurrenceEndDate, Integer recurrenceInterval) {
        scheduledTransferRepository.save(
                new ScheduledTransfer(accountOrigin, accountDestination, amount, concept,
                        scheduledAt, targetTimezone, recurrence, recurrenceEndDate, recurrenceInterval));
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

        int interval = scheduled.getRecurrenceInterval() != null
                ? scheduled.getRecurrenceInterval()
                : 1;

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

            case EVERY_X_DAYS ->
                current.plusDays(interval)
                        .toInstant();

            case EVERY_X_WEEKS ->
                current.plusWeeks(interval)
                        .toInstant();

            case EVERY_X_MONTHS ->
                current.plusMonths(interval)
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
                executed.getRecurrenceEndDate(),
                executed.getRecurrenceInterval()));
        accountService.addReservedBalance(executed.getAccountOrigin(), executed.getAmount());
    }

    public Page<ScheduledTransfer> getFailedScheduledTransfers(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return scheduledTransferRepository.findAllFailed(pageable);
    }

    @Transactional
    public void retryFailedScheduledTransfer(Long id) {
        ScheduledTransfer scheduled = scheduledTransferRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (scheduled.getStatus() != ScheduledTransactionType.FAILED) {
            throw new IllegalStateException("Solo se pueden reintentar transferencias con estado FAILED");
        }

        Account origin = scheduled.getAccountOrigin();
        Account destination = scheduled.getAccountDestination();

        // Validar saldo disponible (balance - reservado)
        double available = origin.getBalance() - origin.getBalanceReserved();
        if (available < scheduled.getAmount()) {
            throw new IllegalArgumentException("Saldo insuficiente para reintentar la transferencia");
        }

        // Ejecutar movimiento de fondos
        accountService.subtractBalance(origin, scheduled.getAmount());
        accountService.addBalance(destination, scheduled.getAmount());

        // Crear la transacción asociada
        Transaction transaction = transactionRepository.save(new Transaction(
                scheduled.getConcept(),
                scheduled.getAmount(),
                Instant.now(),
                TransactionType.TRANSFER,
                origin.getUser(),
                destination,
                origin,
                scheduled));

        // Marcar como ejecutada
        scheduled.setStatus(ScheduledTransactionType.EXECUTED);
        scheduled.setTransaction(transaction);
        scheduledTransferRepository.save(scheduled);
    }
}
