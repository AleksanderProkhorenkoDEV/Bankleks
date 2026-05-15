package com.example.back.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.back.dto.transaction.transaction.CreateTransactionRequestDTO;
import com.example.back.dto.transaction.transaction.CreateTransactionScheduledRequestDTO;
import com.example.back.dto.transaction.transaction.UpdateConceptRequestDTO;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.Transaction;
import com.example.back.entities.user.User;
import com.example.back.enums.TransactionType;
import com.example.back.repositories.TransactionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class TransactionServices {

    private TransactionRepository transactionRepository;
    private AccountService accountService;
    private TransactionScheduledService transactionScheduledService;
    private UserService userService;

    public TransactionServices(TransactionRepository transactionRepository, AccountService accountService,
            UserService userService, TransactionScheduledService transactionScheduledService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.userService = userService;
        this.transactionScheduledService = transactionScheduledService;
    }

    public Page<Transaction> getAllTransaction(String email, Integer page, Integer sizePerPage) {
        User user = userService.getUser(email);
        Pageable pageable = PageRequest.of(page, sizePerPage);
        return transactionRepository.findAllByUserInvolved(user, pageable);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void createTransaction(CreateTransactionRequestDTO request, String email) {
        User user = userService.getUser(email);

        Account destinationAccount = request.getDestinationIban() != null
                ? accountService.getAccountByIban(request.getDestinationIban())
                : null;
        Account originAccount = request.getOriginIban() != null
                ? accountService.getAccountByIban(request.getOriginIban())
                : null;

        switch (request.getTransactionType()) {
            case DEPOSIT -> depositTransaction(request, user, destinationAccount);
            case WITHDRAWAL -> withdrawalTransaction(request, user, originAccount);
            case TRANSFER -> transferTransaction(request, user, destinationAccount, originAccount);
        }
    }

    private void depositTransaction(CreateTransactionRequestDTO request, User user, Account destination) {
        if (destination == null)
            throw new IllegalArgumentException("La cuenta destino no puede ser nula.");

        accountService.addBalance(destination, request.getAmount());
        transactionRepository.save(new Transaction(
                request.getConcept(), request.getAmount(), Instant.now(),
                TransactionType.DEPOSIT, user, destination, null, null));
    }

    private void withdrawalTransaction(CreateTransactionRequestDTO request, User user, Account origin) {
        if (origin == null)
            throw new IllegalArgumentException("La cuenta origen no puede ser nula.");

        accountService.subtractBalance(origin, request.getAmount());
        transactionRepository.save(new Transaction(
                request.getConcept(), request.getAmount(), Instant.now(),
                TransactionType.WITHDRAWAL, user, null, origin, null));
    }

    private void transferTransaction(CreateTransactionRequestDTO request, User user, Account destination,
            Account origin) {
        if (origin == null || destination == null)
            throw new IllegalArgumentException("La cuenta destino u origen no pueden ser nulas.");

        accountService.subtractBalance(origin, request.getAmount());
        accountService.addBalance(destination, request.getAmount());
        transactionRepository.save(new Transaction(
                request.getConcept(), request.getAmount(), Instant.now(),
                TransactionType.TRANSFER, user, destination, origin, null));
    }

    @Transactional
    public void updateConcept(UpdateConceptRequestDTO request) {
        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(EntityNotFoundException::new);

        transaction.setConcept(request.getConcept());
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        switch (transaction.getType()) {
            case DEPOSIT -> {
                // el ingreso sumó dinero al destino → lo quitamos
                if (transaction.getAccountDestination() != null) {
                    accountService.subtractBalance(transaction.getAccountDestination(), transaction.getAmount());
                }
            }
            case WITHDRAWAL -> {
                // la retirada quitó dinero al origen → lo devolvemos
                if (transaction.getAccountOrigin() != null) {
                    accountService.addBalance(transaction.getAccountOrigin(), transaction.getAmount());
                }
            }
            case TRANSFER -> {
                // la transferencia quitó del origen y sumó al destino → revertimos ambos
                if (transaction.getAccountOrigin() != null) {
                    accountService.addBalance(transaction.getAccountOrigin(), transaction.getAmount());
                }
                if (transaction.getAccountDestination() != null) {
                    accountService.subtractBalance(transaction.getAccountDestination(), transaction.getAmount());
                }
            }
        }

        transactionRepository.delete(transaction);
    }

    @Transactional
    public void createScheduledTransaction(CreateTransactionScheduledRequestDTO request, String email) {

        validateTimezone(request.getTargetTimezone());

        Account originAccount = accountService.getAccountByIban(request.getOriginIban());
        Account destinationAccount = accountService.getAccountByIban(request.getDestinationIban());

        Instant recurrenceEndDateUTC = request.getRecurrenceEndDate() != null
                ? convertToUTC(request.getTargetTimezone(), request.getRecurrenceEndDate())
                : null;

        if (request.getRecurrence() != null) {
            // Modo recurrente — solo necesita una fecha de inicio
            LocalDateTime startDateTime = LocalDateTime.parse(
                    request.getScheduledDates().get(0) + "T" + request.getScheduledTime());
            Instant scheduledAtUTC = convertToUTC(request.getTargetTimezone(), startDateTime);

            validateFutureDate(scheduledAtUTC);
            validateAvailableBalance(originAccount, request.getAmount());

            accountService.addReservedBalance(originAccount, request.getAmount());
            transactionScheduledService.createScheduledTransfer(
                    originAccount, destinationAccount,
                    request.getAmount(), request.getConcept(),
                    scheduledAtUTC, request.getTargetTimezone(),
                    request.getRecurrence(), recurrenceEndDateUTC);
        } else {
            // Modo fechas sueltas o rango
            double totalAmount = request.getAmount() * request.getScheduledDates().size();
            validateAvailableBalance(originAccount, totalAmount);

            /*
             * 1. Convertimos la lista de fechas en Instants UTC
             * 2. Creamos con la fecha y el tiempo el LocalDateTime y lo convertimos a UTC
             * 3. Con peek() validamos cada fecha sin transformarla
             * 4. Con toList() forzamos la evaluación del stream — sin esto map y peek no se
             * ejecutan
             */
            List<Instant> scheduledInstants = request.getScheduledDates().stream()
                    .map(date -> convertToUTC(
                            request.getTargetTimezone(),
                            LocalDateTime.parse(date + "T" + request.getScheduledTime())))
                    .peek(this::validateFutureDate)
                    .toList();

            for (Instant scheduledAtUTC : scheduledInstants) {
                accountService.addReservedBalance(originAccount, request.getAmount());
                transactionScheduledService.createScheduledTransfer(
                        originAccount, destinationAccount,
                        request.getAmount(), request.getConcept(),
                        scheduledAtUTC, request.getTargetTimezone(),
                        null, null);
            }
        }
    }

    private void validateAvailableBalance(Account account, Double amount) {
        double available = account.getBalance() - account.getBalanceReserved();
        if (available < amount)
            throw new IllegalArgumentException("Saldo insuficiente");
    }

    private void validateTimezone(String timezone) {
        if (!ZoneId.getAvailableZoneIds().contains(timezone))
            throw new IllegalArgumentException("Timezone no válido: " + timezone);
    }

    private void validateFutureDate(Instant scheduledAt) {
        if (scheduledAt.isBefore(Instant.now()))
            throw new IllegalArgumentException("La fecha debe ser futura");
    }

    private Instant convertToUTC(String targetTime, LocalDateTime scheduledAt) {
        ZonedDateTime userTime = ZonedDateTime.of(
                scheduledAt,
                ZoneId.of(targetTime));

        return userTime.toInstant();
    }

    @Transactional
    public Transaction createTransferTransaction(String concept, Double amount,
            Account origin, Account destination) {
        return transactionRepository.save(new Transaction(
                concept, amount, Instant.now(),
                TransactionType.TRANSFER,
                origin.getUser(),
                destination,
                origin,
                null));
    }
}
