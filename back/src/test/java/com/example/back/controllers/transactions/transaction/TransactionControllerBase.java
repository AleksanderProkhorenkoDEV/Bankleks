package com.example.back.controllers.transactions.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.back.dto.mappers.TransactionMapper;
import com.example.back.repositories.TransactionRepository;
import com.example.back.services.AccountService;
import com.example.back.services.JwtService;
import com.example.back.services.TransactionServices;
import com.example.back.services.UserDetailsServiceImpl;
import com.example.back.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransactionControllerBase {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected TransactionServices transactionServices;

    @MockitoBean
    protected TransactionMapper transactionMapper;

    @MockitoBean
    protected TransactionRepository transactionRepository;

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
