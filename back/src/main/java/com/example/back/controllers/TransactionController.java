package com.example.back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.PageResponseDTO;
import com.example.back.dto.mappers.TransactionMapper;
import com.example.back.dto.transaction.transaction.CreateTransactionRequestDTO;
import com.example.back.dto.transaction.transaction.TransactionResponseDTO;
import com.example.back.services.TransactionServices;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private TransactionServices transactionServices;
    private TransactionMapper transactionMapper;

    public TransactionController(TransactionServices transactionServices, TransactionMapper transactionMapper) {
        this.transactionMapper = transactionMapper;
        this.transactionServices = transactionServices;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PageResponseDTO<TransactionResponseDTO>> getTransactionByUserId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Page<TransactionResponseDTO> dtoPage = transactionServices.getAllTransaction(id, page, size)
                .map(transactionMapper::toDto);

        return ResponseEntity.ok(new PageResponseDTO<>(dtoPage));
    }
    
    @PostMapping("/create")
    public ResponseEntity<String> postMethodName(@Valid @RequestBody CreateTransactionRequestDTO request) {
        transactionServices.createTransaction(request);
        return ResponseEntity.ok("Transacción creada");
    }

}
