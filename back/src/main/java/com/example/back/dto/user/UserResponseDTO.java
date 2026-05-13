package com.example.back.dto.user;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String iban;
    private Double balance;

    public UserResponseDTO(Long id, String name, String email, String role, String iban, Double balance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.iban = iban;
        this.balance = balance;
    }
}
