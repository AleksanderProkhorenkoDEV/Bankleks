package com.example.back.entities.transactions;

import com.example.back.entities.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double balance;

    @Column(nullable = false)
    private String accountNumer;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public Account() {
    }

    public Account(Double balance, User user, String accountNumber) {

        if(balance == null) throw new IllegalArgumentException("El balance no puede ser nulo");
        if(user == null) throw new IllegalArgumentException("El user no puede ser nulo");
        if(accountNumber == null) throw new IllegalArgumentException("El número no puede ser nulo");

        this.balance = balance;
        this.user = user;
        this.accountNumer = accountNumber;
    }

}
