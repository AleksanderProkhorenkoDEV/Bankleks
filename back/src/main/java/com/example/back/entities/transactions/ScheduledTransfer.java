package com.example.back.entities.transactions;

import java.time.Instant;

import com.example.back.enums.ScheduledTransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class ScheduledTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double balance;

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

    @Column(nullable = false)
    private ScheduledTransactionType status;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
