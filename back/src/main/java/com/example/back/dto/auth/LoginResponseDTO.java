package com.example.back.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private String userName;
    private String rol;

    public LoginResponseDTO(String token, String userName, String rol) {
        this.token = token;
        this.userName = userName;
        this.rol = rol;
    }
}
