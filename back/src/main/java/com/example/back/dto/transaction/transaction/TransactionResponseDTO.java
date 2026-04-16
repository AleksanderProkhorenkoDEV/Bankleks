package com.example.back.dto.transaction.transaction;

import java.util.Date;

import com.example.back.dto.transaction.account.AccountSummaryDTO;
import com.example.back.dto.user.UserSummaryDTO;
import com.example.back.enums.TransactionType;

import lombok.Data;

@Data
public class TransactionResponseDTO {

    private Long id;
    private Double amount;
    private TransactionType transactionType;
    private Date transactionDate;
    private AccountSummaryDTO originAccount;
    private AccountSummaryDTO destinationAccount;
    private UserSummaryDTO user;
}
