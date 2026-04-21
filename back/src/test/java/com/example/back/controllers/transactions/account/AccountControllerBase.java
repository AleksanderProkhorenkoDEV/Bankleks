package com.example.back.controllers.transactions.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.back.repositories.AccountRepository;
import com.example.back.services.AccountService;
import com.example.back.services.JwtService;
import com.example.back.services.UserDetailsServiceImpl;
import com.example.back.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AccountControllerBase {
    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected AccountRepository accountRepository;

    @MockitoBean
    protected AccountService accountService;

    @MockitoBean
    protected UserDetailsServiceImpl userDetailsServiceImpl;

    @MockitoBean
    protected AuthenticationManager authenticationManager;

    @MockitoBean
    protected JwtService jwtService;

    protected ObjectMapper objectMapper = new ObjectMapper();

}
