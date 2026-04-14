package com.example.back.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDTO {

    private String message;
    private Integer status;
    private Map<String, String> errors;

    public ErrorResponseDTO(String message, Integer status) {
        this.message = message;
        this.status = status;
    }

    public ErrorResponseDTO(String message, Integer status, Map<String, String> errors) {
        this.message = message;
        this.status = status;
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
