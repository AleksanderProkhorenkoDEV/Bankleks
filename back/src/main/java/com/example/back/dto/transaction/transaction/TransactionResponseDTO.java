package com.example.back.dto.transaction.transaction;

import java.time.Instant;

import com.example.back.dto.transaction.account.AccountSummaryDTO;
import com.example.back.dto.user.UserSummaryDTO;
import com.example.back.enums.RecurrenceType;
import com.example.back.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class TransactionResponseDTO {

    private Long id;
    private Double amount;
    private String concept;
    private TransactionType transactionType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant transactionDate;
    private AccountSummaryDTO originAccount;
    private AccountSummaryDTO destinationAccount;
    private UserSummaryDTO user;
    private boolean scheduled;
    private RecurrenceType recurrenceType;
}
