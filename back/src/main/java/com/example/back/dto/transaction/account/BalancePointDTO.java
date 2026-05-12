package com.example.back.dto.transaction.account;

import java.time.Instant;

import lombok.Data;

@Data
public class BalancePointDTO {
    
    private Instant date;
    private Double balance;

    
    public BalancePointDTO(Instant date, Double balance) {
        this.date = date;
        this.balance = balance;
    }

    
}
