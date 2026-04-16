package com.example.back.dto.transaction.account;

import lombok.Data;

@Data
public class CreateAccountResponseDTO {

    private String message;

    public CreateAccountResponseDTO() {
    }

    public CreateAccountResponseDTO(String message) {
        this.message = message;
    }

}
