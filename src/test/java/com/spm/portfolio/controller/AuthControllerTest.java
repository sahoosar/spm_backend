package com.spm.portfolio.controller;

import com.spm.portfolio.dto.LoginRequestDto;
import com.spm.portfolio.model.User;
import com.spm.portfolio.repository.UserRepository;
import com.spm.portfolio.service.user.CustomUserDetailsService;
import com.spm.portfolio.service.user.UserService;
import com.spm.portfolio.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
public class AuthControllerTest {
    private WebTestClient webTestClient;
    private CustomUserDetailsService userDetailsService;
    private JwtUtil jwtUtil;
    private BCryptPasswordEncoder passwordEncoder;


    @BeforeEach
    public void setUp() {
        // Create mocks for dependencies.
        userDetailsService = mock(CustomUserDetailsService.class);
        jwtUtil = mock(JwtUtil.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);

        // Instantiate the AuthController with the mocked dependencies.
        AuthController authController = new AuthController(userDetailsService, jwtUtil, passwordEncoder);

        // Bind WebTestClient to the controller.
        webTestClient = WebTestClient.bindToController(authController)
                .configureClient()
                .baseUrl("/auth")
                .build();
    }


    @Test
    public void testValidateToken() {
        // Arrange: Stub jwtUtil.validateToken to return true.
        String token = "dummy-token";
        when(jwtUtil.validateToken(token)).thenReturn(true);

        // Act & Assert: Call GET /auth/token with the token in the Authorization header.
        webTestClient.get()
                .uri("/token")
                .header("Authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .value(b -> assertEquals(true, b, "Token validation should return true"));
    }

    @Test
    public void testLoginSuccess() {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUserId("user123");
        loginRequest.setPassword("rawpassword");

        // Prepare a user object that represents what is stored in your database.
        User userDetails = new User();
        userDetails.setUserId("user123");
        String encodedPassword = "encodedPassword";
        userDetails.setPassword(encodedPassword);

        // Stub the userDetailsService and password encoder.
        when(userDetailsService.findByUserId("user123")).thenReturn(Mono.just(userDetails));
        when(passwordEncoder.matches("rawpassword", encodedPassword)).thenReturn(true);

        // Stub JWT generation.
        String jwtToken = "Bearer";
        when(jwtUtil.generateToken("user123")).thenReturn(jwtToken);

        // Assert: POST /auth/login should return the generated token.
        webTestClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(jwtToken);
    }

}