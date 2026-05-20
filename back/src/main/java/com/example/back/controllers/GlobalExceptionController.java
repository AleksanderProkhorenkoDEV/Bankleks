package com.example.back.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.back.dto.ErrorResponseDTO;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionController {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                return ResponseEntity.badRequest().body(
                                new ErrorResponseDTO(
                                                "Error de validación",
                                                HttpStatus.BAD_REQUEST.value(),
                                                errors));

        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponseDTO> handleBadCredentialsError() {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new ErrorResponseDTO("Credenciales no validas", HttpStatus.UNAUTHORIZED.value()));
        }

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ErrorResponseDTO> handleEntityNotFoundError() {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorResponseDTO("Entidad no encontrada", HttpStatus.NOT_FOUND.value()));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentError(IllegalArgumentException ex) {
                return ResponseEntity.badRequest()
                                .body(new ErrorResponseDTO(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<ErrorResponseDTO> handleIllegalStateError(IllegalStateException ex) {
                return ResponseEntity.badRequest()
                                .body(new ErrorResponseDTO(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
}
