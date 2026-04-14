package com.example.back.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefreshResponseDTO {
    private String message;
    private String newAccessToken;

    public RefreshResponseDTO(String message) {
        this.message = message;
    }

    public RefreshResponseDTO(String message, String newAccessToken) {
        this.message = message;
        this.newAccessToken = newAccessToken;
    }

}
