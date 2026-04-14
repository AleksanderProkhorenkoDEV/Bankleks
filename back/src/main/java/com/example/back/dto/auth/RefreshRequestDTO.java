package com.example.back.dto.auth;

import jakarta.validation.constraints.NotEmpty;

public class RefreshRequestDTO {
    @NotEmpty(message = "{validation.notEmpty}")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
