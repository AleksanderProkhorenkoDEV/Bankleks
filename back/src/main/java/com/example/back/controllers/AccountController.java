package com.example.back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.transaction.account.CreateAccountRequestDTO;
import com.example.back.dto.transaction.account.CreateAccountResponseDTO;
import com.example.back.dto.transaction.account.GetBalanceResponseDTO;
import com.example.back.entities.transactions.Account;
import com.example.back.services.AccountService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/get-balance/{id}")
    public ResponseEntity<GetBalanceResponseDTO> getBalanceAccount(@PathVariable Long id) {
        Account account = this.accountService.getAccount(id);
        return ResponseEntity.ok(new GetBalanceResponseDTO(account.getBalance()));
    }

    @PostMapping("/create-account")
    public ResponseEntity<CreateAccountResponseDTO> createAccount(@Valid @RequestBody CreateAccountRequestDTO request) {
        accountService.createAccount(request);
        return ResponseEntity.ok(new CreateAccountResponseDTO("cuenta creada correctamente"));
    }

}
