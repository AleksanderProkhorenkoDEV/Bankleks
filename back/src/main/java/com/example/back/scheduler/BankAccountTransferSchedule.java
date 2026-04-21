package com.example.back.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.back.entities.transactions.Account;
import com.example.back.services.AccountService;

@Component
public class BankAccountTransferSchedule {

    private AccountService accountService;

    public BankAccountTransferSchedule(AccountService accountService) {
        this.accountService = accountService;
    }
    
    @Scheduled(cron = "0 */5 * * * *")
    public void insertMoney() {
        Account account = accountService.getRandomAccount();
        accountService.addBalance(account, 2000.00);
    }
}
