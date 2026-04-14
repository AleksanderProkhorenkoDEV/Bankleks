package com.example.back.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

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

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
