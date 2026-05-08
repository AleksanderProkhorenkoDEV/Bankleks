package com.example.back.dto.transaction.transaction;

import java.time.Instant;

import com.example.back.dto.transaction.account.AccountSummaryDTO;
import com.example.back.dto.user.UserSummaryDTO;
import com.example.back.enums.TransactionType;

import lombok.Data;

@Data
public class TransactionResponseDTO {

    private Long id;
    private Double amount;
    private String concept;
    private TransactionType transactionType;
    private Instant transactionDate;
    private AccountSummaryDTO originAccount;
    private AccountSummaryDTO destinationAccount;
    private UserSummaryDTO user;
}
