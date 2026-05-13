package com.example.back.controllers.transactions.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.back.controllers.GlobalExceptionController;
import com.example.back.controllers.TransactionController;
import com.example.back.dto.mappers.TransactionMapper;
import com.example.back.services.AccountService;
import com.example.back.services.JwtService;
import com.example.back.services.TransactionServices;
import com.example.back.services.UserDetailsServiceImpl;
import com.example.back.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class TransactionControllerBase {
    @Autowired
    private TransactionController transactionController;

    @Autowired
    private GlobalExceptionController globalExceptionController;

    protected MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(transactionController, globalExceptionController)
                .setCustomArgumentResolvers(
                        new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @MockitoBean
    protected TransactionServices transactionServices;

    @MockitoBean
    protected TransactionMapper transactionMapper;

    @MockitoBean
    protected AccountService accountService;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected UserDetailsServiceImpl userDetailsServiceImpl;

    @MockitoBean
    protected AuthenticationManager authenticationManager;

    @MockitoBean
    protected JwtService jwtService;

    protected ObjectMapper objectMapper = new ObjectMapper();
}
