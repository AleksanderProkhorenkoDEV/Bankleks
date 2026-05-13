package com.example.back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.mappers.AccountMapper;
import com.example.back.dto.transaction.account.AccountResponseDTO;
import com.example.back.dto.transaction.account.AccountStatsDTO;
import com.example.back.dto.transaction.account.AccountTimezoeResponseDTO;
import com.example.back.dto.transaction.account.GetBalanceResponseDTO;
import com.example.back.entities.transactions.Account;
import com.example.back.services.AccountService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public AccountController(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<GetBalanceResponseDTO> getBalanceAccount(@PathVariable Long id) {
        Account account = this.accountService.getAccount(id);
        return ResponseEntity.ok(new GetBalanceResponseDTO(account.getBalance()));
    }

    @GetMapping("/{id}/all-data")
    public ResponseEntity<AccountResponseDTO> getMethodName(@PathVariable Long id) {
        Account account = accountService.getAccount(id);
        return ResponseEntity.ok(accountMapper.toDTO(account));
    }

    @GetMapping("/stats")
    public ResponseEntity<AccountStatsDTO> getStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(accountService.getStats(userDetails.getUsername()));
    }

    @GetMapping("/{id}/timezone")
    public ResponseEntity<AccountTimezoeResponseDTO> getTimezone(@PathVariable Long id) {
        Account account = accountService.getAccount(id);
        return ResponseEntity.ok(new AccountTimezoeResponseDTO(account.getTimezone()));
    }

    @PatchMapping("/{id}/timezone")
    public ResponseEntity<Void> updateTimezone(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        accountService.updateTimezone(id, body.get("timezone"));
        return ResponseEntity.noContent().build();
    }

}
