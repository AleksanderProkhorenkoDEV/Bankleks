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

        double totalAmount = request.getAmount() * request.getScheduledDates().size();
        validateAvailableBalance(originAccount, totalAmount);

        /* 
         1 Convertimos la lista de fechas en Instants UTC 
         2 Creamos con la fecha y el tiempo el LocalDateTiem y lo convertimos a UTC
         3 con peek() ejecuta la acción por cada elemento sin transformarlo, así nos aseguramos de que las fechas sean válidas
         4 con toList() forzamos que el stream sea una lista, sin esto no se ejecuta el map ni el peek
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
                    scheduledAtUTC, request.getTargetTimezone());
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
