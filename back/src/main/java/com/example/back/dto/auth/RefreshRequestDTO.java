package com.example.back.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequestDTO {
    @NotEmpty(message = "{validation.notEmpty}")
    private String refreshToken;

    public RefreshRequestDTO(@NotEmpty(message = "{validation.notEmpty}") String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
