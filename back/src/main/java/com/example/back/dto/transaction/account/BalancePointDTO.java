package com.example.back.dto.transaction.account;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BalancePointDTO {
    
    private LocalDate date;
    private Double balance;

    
    public BalancePointDTO(LocalDate date, Double balance) {
        this.date = date;
        this.balance = balance;
    }

    
}
