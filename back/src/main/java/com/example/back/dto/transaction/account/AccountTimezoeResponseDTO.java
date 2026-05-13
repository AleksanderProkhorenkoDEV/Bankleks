package com.example.back.dto.transaction.account;

import lombok.Data;

@Data
public class AccountTimezoeResponseDTO {
    private String timezone;

    public AccountTimezoeResponseDTO(String timezone) {
        this.timezone = timezone;
    }

}
