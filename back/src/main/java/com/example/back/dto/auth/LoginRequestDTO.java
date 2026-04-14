package com.example.back.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class LoginRequestDTO {
    @Email(message = "El email no esta bien formado.")
    @NotEmpty(message = "No puede estar vacio el campo del email")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
