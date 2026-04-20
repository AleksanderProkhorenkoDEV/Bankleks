package com.example.back.dto.transaction.account;

import lombok.Data;

@Data
public class AccountResponseDTO {

    private Long id;
    private String accountNumber;
    private Double balance;
    private Long userId;

}
