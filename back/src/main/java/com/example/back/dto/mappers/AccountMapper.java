package com.example.back.dto.mappers;

import org.springframework.stereotype.Component;

import com.example.back.dto.transaction.account.AccountResponseDTO;
import com.example.back.dto.user.UserSummaryDTO;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.user.User;

@Component
public class AccountMapper {
    public AccountResponseDTO toDTO(Account account) {
        UserSummaryDTO userDTO = toUserSummaryDTO(account.getUser());
        return new AccountResponseDTO(account.getAccountNumber(), account.getBalance(), userDTO);
    }

    private UserSummaryDTO toUserSummaryDTO(User user) {
        return new UserSummaryDTO(user.getId(), user.getName());
    }
}
