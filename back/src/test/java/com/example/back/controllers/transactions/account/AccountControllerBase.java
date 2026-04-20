package com.example.back.controllers.transactions.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.back.repositories.AccountRepository;
import com.example.back.services.AccountService;
import com.example.back.services.UserService;


@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerBase {
    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected AccountRepository accountRepository;

    @MockitoBean
    protected AccountService accountService;

}
