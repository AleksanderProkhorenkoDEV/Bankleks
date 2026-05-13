package com.example.back.entities.transactions;

import java.time.Instant;

import com.example.back.entities.user.User;
import com.example.back.enums.TransactionType;

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
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String concept;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Instant executedAt;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_destination_id", nullable = true)
    private Account accountDestination;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_origin_id", nullable = true)
    private Account accountOrigin;

    @OneToOne
    @JoinColumn(name = "scheduled_transfer_id", nullable = true)
    private ScheduledTransfer scheduledTransfer;

    public Transaction() {
    }

    public Transaction(String concept, Double amount, Instant executedAt, TransactionType type, User user,
            Account accountDestination, Account accountOrigin, ScheduledTransfer scheduledTransfer) {
        this.concept = concept;
        this.amount = amount;
        this.executedAt = executedAt;
        this.type = type;
        this.user = user;
        this.accountDestination = accountDestination;
        this.accountOrigin = accountOrigin;
        this.scheduledTransfer = scheduledTransfer;
    }

}
