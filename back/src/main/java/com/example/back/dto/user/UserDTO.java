package com.example.back.dto.user;

import com.example.back.dto.role.RoleSummaryDTO;

import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String email;
    private String name;
    private String password;
    private RoleSummaryDTO rolName;
}