package com.example.back.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @Email(message = "{validation.email}")
    @NotEmpty(message = "{validation.notEmpty}")
    private String email;
    @NotEmpty(message = "{validation.notEmpty}")
    private String name;

    @NotBlank(message = "{validation.notBlank}")
    @Size(min = 8, message = "{validation.password.size}")
    private String password;

    public RegisterRequestDTO(
            @Email(message = "{validation.email}") @NotEmpty(message = "{validation.notEmpty}") String email,
            @NotEmpty(message = "{validation.notEmpty}") String name,
            @NotBlank(message = "{validation.notBlank}") @Size(min = 8, message = "{validation.password.size}") String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

}
