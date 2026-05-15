package com.example.back.dto.mappers;

import org.springframework.stereotype.Component;

import com.example.back.dto.transaction.account.AccountSummaryDTO;
import com.example.back.dto.transaction.transaction.TransactionResponseDTO;
import com.example.back.dto.user.UserSummaryDTO;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.Transaction;
import com.example.back.entities.user.User;

@Component
public class TransactionMapper {

    public TransactionResponseDTO toDto(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();

        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setConcept(transaction.getConcept());
        dto.setTransactionType(transaction.getType());
        dto.setTransactionDate(transaction.getExecutedAt());
        dto.setOriginAccount(toAccountSummary(transaction.getAccountOrigin()));
        dto.setDestinationAccount(toAccountSummary(transaction.getAccountDestination()));
        dto.setUser(toUserSummary(transaction.getUser()));
        dto.setScheduled(transaction.getScheduledTransfer() != null);
        dto.setRecurrenceType(transaction.getScheduledTransfer() != null
                ? transaction.getScheduledTransfer().getRecurrence()
                : null);

        return dto;
    }

    private AccountSummaryDTO toAccountSummary(Account account) {
        if (account == null)
            return null;
        AccountSummaryDTO dto = new AccountSummaryDTO();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        return dto;
    }

    private UserSummaryDTO toUserSummary(User user) {
        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getName());
        return dto;
    }
}
