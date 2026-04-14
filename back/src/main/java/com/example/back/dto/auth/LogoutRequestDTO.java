package com.example.back.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LogoutRequestDTO {
    @NotEmpty(message = "{validation.notEmpty}")
    private String refreshToken;

    public LogoutRequestDTO(String refreshToken){
        this.refreshToken = refreshToken;
    }
}
