package com.example.back.entities.transactions;

import java.util.ArrayList;
import java.util.List;

import com.example.back.entities.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
    private String accountNumber;

    @Column(nullable = false, length = 50)
    private String timezone = "UTC";

    @Column(nullable = false)
    private Double balanceReserved = 0D;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "accountOrigin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduledTransfer> originScheduledTransfers = new ArrayList<>();

    @OneToMany(mappedBy = "accountDestination", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduledTransfer> destinationScheduledTransfers = new ArrayList<>();

    public Account() {
    }

    public Account(Double balance, String accountNumber, String timezone, User user) {
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.timezone = timezone;
        this.user = user;
        this.balanceReserved = 0.0;
    }

}
