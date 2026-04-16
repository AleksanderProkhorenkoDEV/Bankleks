package com.example.back.entities.transaction;

import java.sql.Date;

import com.example.back.entities.transactions.Account;
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
    private Date transactionDay;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="account_destination_id")
    private Account accountDestination;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="account_origin_id")
    private Account accountOrigin;
}
