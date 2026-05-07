package com.example.back.controllers.auth;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.example.back.controllers.AuthController;
import com.example.back.controllers.GlobalExceptionController;
import com.example.back.repositories.RefreshTokenRepository;
import com.example.back.repositories.UserRepository;
import com.example.back.services.AuthService;
import com.example.back.services.JwtService;
import com.example.back.services.RefreshTokenService;
import com.example.back.services.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AuthController.class)
@Import(GlobalExceptionController.class)
abstract class AuthControllerBase {

    @Autowired
    private AuthController authController;

    @Autowired
    private GlobalExceptionController globalExceptionController;

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected AuthenticationManager authenticationManager;

    @MockitoBean
    protected JwtService jwtService;

    @MockitoBean
    protected RefreshTokenService refreshTokenService;

    @MockitoBean
    protected UserDetailsServiceImpl userDetailsServiceImpl;

    @MockitoBean
    protected UserRepository userRepository;

    @MockitoBean
    protected RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController, globalExceptionController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
}
