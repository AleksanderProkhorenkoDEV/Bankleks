package com.example.back.dto.transaction.transaction;

import com.example.back.enums.TransactionType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CreateTransactionRequestDTO {

    @NotEmpty(message = "{validation.notEmpty}")
    private String concept;

    @NotNull(message = "{validation.notNull}")
    @PositiveOrZero
    private Double amount;

    private String destinationIban;
    private String originIban;

    @NotNull(message = "{validation.notNull}")
    private TransactionType transactionType;

    public CreateTransactionRequestDTO(@NotEmpty(message = "{validation.notEmpty}") String concept,
            @NotNull(message = "{validation.notNull}") @PositiveOrZero Double amount, String destinationIban,
            String originIban, @NotNull(message = "{validation.notNull}") TransactionType transactionType) {
        this.concept = concept;
        this.amount = amount;
        this.destinationIban = destinationIban;
        this.originIban = originIban;
        this.transactionType = transactionType;
    }

}
