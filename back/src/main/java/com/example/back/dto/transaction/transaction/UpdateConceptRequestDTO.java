package com.example.back.dto.transaction.transaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateConceptRequestDTO {

    @Size(min = 4, max = 254, message = "{validation.size}")
    @NotNull(message = "{validation.notNull}")
    private String concept;

    @NotNull(message = "{validation.notNull}")
    private Long transactionId;
}
