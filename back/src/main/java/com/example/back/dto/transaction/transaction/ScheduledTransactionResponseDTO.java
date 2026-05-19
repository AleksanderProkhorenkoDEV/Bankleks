package com.example.back.dto.transaction.transaction;

import java.time.Instant;

import com.example.back.dto.transaction.account.AccountSummaryDTO;
import com.example.back.enums.RecurrenceType;
import com.example.back.enums.ScheduledTransactionType;

import lombok.Data;

@Data
public class ScheduledTransactionResponseDTO {
    private Long id;
    private Double amount;
    private String concept;
    private AccountSummaryDTO accountOrigin;
    private AccountSummaryDTO accountDestination;
    private Instant scheduledAt;
    private String targetTimezone;
    private ScheduledTransactionType status;
    private RecurrenceType recurrence;
    private Instant recurrenceEndDate;
    private Instant createdAt;

    public ScheduledTransactionResponseDTO(Long id, Double amount, String concept, AccountSummaryDTO accountOrigin,
            AccountSummaryDTO accountDestination, Instant scheduledAt, String targetTimezone,
            ScheduledTransactionType status, RecurrenceType recurrence, Instant recurrenceEndDate, Instant createdAt) {
        this.id = id;
        this.amount = amount;
        this.concept = concept;
        this.accountOrigin = accountOrigin;
        this.accountDestination = accountDestination;
        this.scheduledAt = scheduledAt;
        this.targetTimezone = targetTimezone;
        this.status = status;
        this.recurrence = recurrence;
        this.recurrenceEndDate = recurrenceEndDate;
        this.createdAt = createdAt;
    }

}
