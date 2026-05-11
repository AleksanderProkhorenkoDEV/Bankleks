package com.example.back.dto.transaction.transaction;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CreateTransactionScheduledRequestDTO {

    @NotEmpty(message = "{validation.notEmpty}")
    private String concept;

    @NotNull(message = "{validation.notNull}")
    @PositiveOrZero
    private Double amount;

    @NotNull(message = "{validation.notNull}")
    private String destinationIban;
    @NotNull(message = "{validation.notNull}")
    private String originIban;

    @NotNull(message = "{validation.notNull}")
    private String targetTimezone;

    @NotNull(message = "{validation.notNull}")
    private LocalDateTime scheduledAt;

    public CreateTransactionScheduledRequestDTO(@NotEmpty(message = "{validation.notEmpty}") String concept,
            @NotNull(message = "{validation.notNull}") @PositiveOrZero Double amount,
            @NotNull(message = "{validation.notNull}") String destinationIban,
            @NotNull(message = "{validation.notNull}") String originIban,
            @NotNull(message = "{validation.notNull}") String targetTimezone,
            @NotNull(message = "{validation.notNull}") LocalDateTime scheduledAt) {
        this.concept = concept;
        this.amount = amount;
        this.destinationIban = destinationIban;
        this.originIban = originIban;
        this.targetTimezone = targetTimezone;
        this.scheduledAt = scheduledAt;
    }

    
}
