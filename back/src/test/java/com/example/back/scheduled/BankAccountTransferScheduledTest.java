package com.example.back.scheduled;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.back.entities.auth.Role;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.user.User;
import com.example.back.scheduler.BankAccountTransferSchedule;
import com.example.back.services.AccountService;

@ExtendWith(MockitoExtension.class)
public class BankAccountTransferScheduledTest {
    @InjectMocks
    private BankAccountTransferSchedule bankAccountTransferSchedule; 

    @Mock
    private AccountService accountService;

    @Test
    void shouldAddBalanceToRandomAccount() {
        User user = new User("test", "test@gmail.com", "123456789", new Role());
        Account account = new Account(500.0, "1234567899876543211234", "UTC", user);

        when(accountService.getRandomAccount()).thenReturn(account);

        bankAccountTransferSchedule.insertMoney();

        verify(accountService).getRandomAccount();
        verify(accountService).addBalance(account, 2000.0);
    }
}
