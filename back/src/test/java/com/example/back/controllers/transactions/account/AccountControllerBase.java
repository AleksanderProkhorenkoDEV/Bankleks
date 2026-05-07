package com.example.back.controllers.transactions.account;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.example.back.controllers.AccountController;
import com.example.back.controllers.GlobalExceptionController;
import com.example.back.dto.mappers.AccountMapper;
import com.example.back.services.AccountService;
import com.example.back.services.JwtService;
import com.example.back.services.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AccountController.class)
@Import(GlobalExceptionController.class)
public abstract class AccountControllerBase {

    @Autowired
    private AccountController accountController;

    @Autowired
    private GlobalExceptionController globalExceptionController;

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    protected AccountService accountService;

    @MockitoBean
    protected AccountMapper accountMapper;

    @MockitoBean
    protected JwtService jwtService;

    @MockitoBean
    protected UserDetailsServiceImpl userDetailsServiceImpl;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(accountController, globalExceptionController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
}
