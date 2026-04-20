package com.example.back.dto;

import lombok.Data;

@Data
public class GlobalResponseDTO {
    private String message;
    private Integer status;

    public GlobalResponseDTO(String message, Integer status) {
        this.message = message;
        this.status = status;
    }
}
