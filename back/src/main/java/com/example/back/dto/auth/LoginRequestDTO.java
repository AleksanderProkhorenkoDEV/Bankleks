package com.example.back.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @Email(message = "{validation.email}")
    @NotEmpty(message = "{validation.notEmpty}")
    private String email;

    @NotEmpty(message = "{validation.notEmpty}")
    private String password;

    public LoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
