package com.example.back.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {

    @Email(message = "{validation.email}")
    @NotEmpty(message = "{validation.notEmpty}")
    private String email;
    @NotEmpty(message = "{validation.notEmpty}")
    private String name;

    @NotBlank(message = "{validation.notBlank}")
    @Size(min = 8, message = "{validation.password.size}")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
