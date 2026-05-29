package com.example.back.entities.transactions;

import java.time.Instant;

import com.example.back.enums.RecurrenceType;
import com.example.back.enums.ScheduledTransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class ScheduledTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String concept;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_origin_id", nullable = false)
    private Account accountOrigin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_destination_id", nullable = false)
    private Account accountDestination;

    @Column(nullable = false)
    private Instant scheduledAt;

    @Column(nullable = false)
    private String targetTimezone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduledTransactionType status;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private RecurrenceType recurrence;

    @Column(nullable = true)
    private Instant recurrenceEndDate;

    @Column(nullable = true)
    private Integer recurrenceInterval;

    @Column(nullable = true)
    private Integer remainingRecurrences;

    public ScheduledTransfer() {
    }

    public ScheduledTransfer(Account accountOrigin, Account accountDestination,
            Double amount, String concept, Instant scheduledAt, String targetTimezone,
            RecurrenceType recurrence, Instant recurrenceEndDate, Integer recurrenceInterval,
            Integer remainingRecurrences) {
        this.accountOrigin = accountOrigin;
        this.accountDestination = accountDestination;
        this.amount = amount;
        this.concept = concept;
        this.scheduledAt = scheduledAt;
        this.targetTimezone = targetTimezone;
        this.status = ScheduledTransactionType.SCHEDULED;
        this.recurrence = recurrence;
        this.recurrenceEndDate = recurrenceEndDate;
        this.recurrenceInterval = recurrenceInterval;
        this.remainingRecurrences = remainingRecurrences;
    }
}
