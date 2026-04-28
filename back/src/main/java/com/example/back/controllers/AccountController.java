package com.example.back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.transaction.account.GetBalanceResponseDTO;
import com.example.back.entities.transactions.Account;
import com.example.back.services.AccountService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/accounts")
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<GetBalanceResponseDTO> getBalanceAccount(@PathVariable Long id) {
        Account account = this.accountService.getAccount(id);
        return ResponseEntity.ok(new GetBalanceResponseDTO(account.getBalance()));
    }

}
