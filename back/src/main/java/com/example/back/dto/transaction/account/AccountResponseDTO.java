package com.example.back.dto.transaction.account;

import com.example.back.dto.user.UserSummaryDTO;

import lombok.Data;

@Data
public class AccountResponseDTO {

    private String accountNumber;
    private Double balance;
    private UserSummaryDTO userSummaryDTO;

    public AccountResponseDTO(String accountNumber, Double balance, UserSummaryDTO userSummaryDTO) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.userSummaryDTO = userSummaryDTO;
    }
}
