package com.example.back.dto.transaction.account;

import lombok.Data;

@Data
public class GetBalanceResponseDTO {

    private Double amount;

    public GetBalanceResponseDTO() {
    }

    public GetBalanceResponseDTO(Double amount) {
        this.amount = amount;
    }

}
