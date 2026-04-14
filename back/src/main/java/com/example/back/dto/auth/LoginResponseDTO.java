package com.example.back.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private String refreshToken;
    private String email;

    public LoginResponseDTO(String token, String refreshToken, String email) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.email = email;
    }
  }
