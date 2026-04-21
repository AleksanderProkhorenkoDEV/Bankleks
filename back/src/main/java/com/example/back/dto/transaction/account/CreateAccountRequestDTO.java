package com.example.back.dto.transaction.account;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAccountRequestDTO {

    @NotEmpty(message = "${validation.notEmpty}")
    @Size(min = 21, max = 22)
    private String accountNumber;

    @NotNull(message = "${validation.notNull}")
    private Long userId;

    @NotNull(message = "${validation.notNull}")
    @PositiveOrZero
    private Double balance;

    public CreateAccountRequestDTO(
            @NotEmpty(message = "${validation.notEmpty}") @Size(min = 21, max = 22) String accountNumber,
            @NotNull(message = "${validation.notNull}") Long userId,
            @NotNull(message = "${validation.notNull}") @PositiveOrZero Double balance) {
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.balance = balance;
    }

}
