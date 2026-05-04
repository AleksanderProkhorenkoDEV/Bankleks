package com.example.back.dto.transaction.account;

import java.util.List;

import lombok.Data;

@Data
public class AccountStatsDTO {

    private Double totalIncome;
    private Double totalExpense;
    List<BalancePointDTO> balancePointDTO;

    public AccountStatsDTO(Double totalIncome, Double totalExpense, List<BalancePointDTO> balancePointDTO) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balancePointDTO = balancePointDTO;
    }

}
