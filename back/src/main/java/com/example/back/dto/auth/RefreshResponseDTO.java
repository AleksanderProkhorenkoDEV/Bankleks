package com.example.back.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

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

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getNewAccessToken() {
        return newAccessToken;
    }

    public void setNewAccessToken(String newAccessToken) {
        this.newAccessToken = newAccessToken;
    }

}
