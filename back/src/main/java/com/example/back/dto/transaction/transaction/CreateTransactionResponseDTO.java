package com.example.back.dto.transaction.transaction;

import lombok.Data;

@Data
public class CreateTransactionResponseDTO {
    private String message;

    public CreateTransactionResponseDTO() {
    }

    public CreateTransactionResponseDTO(String message) {
        this.message = message;
    }

}
