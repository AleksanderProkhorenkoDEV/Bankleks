package com.example.back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.GlobalResponseDTO;
import com.example.back.dto.PageResponseDTO;
import com.example.back.dto.mappers.TransactionMapper;
import com.example.back.dto.transaction.transaction.CreateTransactionRequestDTO;
import com.example.back.dto.transaction.transaction.CreateTransactionScheduledRequestDTO;
import com.example.back.dto.transaction.transaction.TransactionResponseDTO;
import com.example.back.dto.transaction.transaction.UpdateConceptRequestDTO;
import com.example.back.services.TransactionServices;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @GetMapping()
    public ResponseEntity<PageResponseDTO<TransactionResponseDTO>> getTransactionByUserId(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "25") Integer size) {

        Page<TransactionResponseDTO> dtoPage = transactionServices
                .getAllTransaction(userDetails.getUsername(), page, size)
                .map(transactionMapper::toDto);

        return ResponseEntity.ok(new PageResponseDTO<>(dtoPage));
    }

    @PostMapping("/create")
    public ResponseEntity<GlobalResponseDTO> createTransaction(
            @Valid @RequestBody CreateTransactionRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        transactionServices.createTransaction(request, userDetails.getUsername());
        return ResponseEntity.ok(new GlobalResponseDTO("Transacción creada", HttpStatus.CREATED.value()));
    }

    @PostMapping("/create-scheduled")
    public ResponseEntity<GlobalResponseDTO> createTransactionScheduled(
            @Valid @RequestBody CreateTransactionScheduledRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        transactionServices.createScheduledTransaction(request, userDetails.getUsername());
        return ResponseEntity.ok(new GlobalResponseDTO("Transacción creada", HttpStatus.CREATED.value()));
    }

    @PatchMapping("/update")
    public ResponseEntity<GlobalResponseDTO> updateConceptTransaction(
            @Valid @RequestBody UpdateConceptRequestDTO request) {
        transactionServices.updateConcept(request);
        return ResponseEntity.ok(new GlobalResponseDTO("Transacción actualizada", HttpStatus.NO_CONTENT.value()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponseDTO> deleteTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        transactionServices.deleteTransaction(id);
        return ResponseEntity.ok(new GlobalResponseDTO("Transacción eliminada", HttpStatus.OK.value()));
    }

}
