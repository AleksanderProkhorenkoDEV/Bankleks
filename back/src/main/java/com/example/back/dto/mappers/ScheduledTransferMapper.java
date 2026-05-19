package com.example.back.dto.mappers;

import org.springframework.stereotype.Component;

import com.example.back.dto.transaction.account.AccountSummaryDTO;
import com.example.back.dto.transaction.transaction.ScheduledTransactionResponseDTO;
import com.example.back.entities.transactions.ScheduledTransfer;

@Component
public class ScheduledTransferMapper {
    public ScheduledTransactionResponseDTO toDto(ScheduledTransfer s) {

        AccountSummaryDTO origin = null;
        if (s.getAccountOrigin() != null) {
            origin = new AccountSummaryDTO();
            origin.setAccountNumber(s.getAccountOrigin().getAccountNumber());
        }

        AccountSummaryDTO destination = null;
        if (s.getAccountDestination() != null) {
            destination = new AccountSummaryDTO();
            destination.setAccountNumber(s.getAccountDestination().getAccountNumber());
        }

        return new ScheduledTransactionResponseDTO(
                s.getId(),
                s.getAmount(),
                s.getConcept(),
                origin,
                destination,
                s.getScheduledAt(),
                s.getTargetTimezone(),
                s.getStatus(),
                s.getRecurrence(),
                s.getRecurrenceEndDate(),
                s.getCreatedAt());
    }
}
