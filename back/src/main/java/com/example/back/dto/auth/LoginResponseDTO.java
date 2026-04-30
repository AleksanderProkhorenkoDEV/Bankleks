package com.example.back.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private String userName;
    private String rol;
    private Long id;
    private String iban;

    public LoginResponseDTO(String token, String userName, String rol, Long id, String iban) {
        this.token = token;
        this.userName = userName;
        this.rol = rol;
        this.id = id;
        this.iban = iban;
    }
}
