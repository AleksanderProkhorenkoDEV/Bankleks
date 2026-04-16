package com.example.back.dto.transaction.transaction;

import java.sql.Date;

import com.example.back.enums.TransactionType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CreateTransactionRequestDTO {

    @NotEmpty(message = "{validation.notEmpty}")
    private String concept;

    @NotNull(message = "${validation.notNull}")
    @PositiveOrZero
    private Double amount;

    @NotNull(message = "{validation.notNull}")
    @PositiveOrZero
    private Long destinationAccountId;

    @NotNull(message = "{validation.notNull}")
    @PositiveOrZero
    private Long originAccountId;

    @NotNull(message = "{validation.notNull}")
    @PositiveOrZero
    private Long userId;

    @NotNull(message = "{validation.notNullt}")
    private TransactionType transactionType;

    public CreateTransactionRequestDTO() {
    }

    public CreateTransactionRequestDTO(@NotEmpty(message = "{validation.notEmpty}") String concept,
            @NotNull(message = "${validation.notNull}") @PositiveOrZero Double amount,
            @NotNull(message = "{validation.notNull}") @PositiveOrZero Long destinationAccountId,
            @NotNull(message = "{validation.notNull}") @PositiveOrZero Long originAccountId,
            @NotNull(message = "{validation.notNull}") @PositiveOrZero Long userId,
            @NotNull(message = "{validation.notNullt}") TransactionType transactionType) {
        this.concept = concept;
        this.amount = amount;
        this.destinationAccountId = destinationAccountId;
        this.originAccountId = originAccountId;
        this.userId = userId;
        this.transactionType = transactionType;
    }

}
