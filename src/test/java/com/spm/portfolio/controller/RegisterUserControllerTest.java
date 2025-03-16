package com.spm.portfolio.controller;

import com.spm.portfolio.model.User;
import com.spm.portfolio.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegisterUserControllerTest {

    BCryptPasswordEncoder bCryptPasswordEncoder;
    private WebTestClient webTestClient;
    private UserService userService;


    private User mockUser;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
        RegisterUserController registerUserController = new RegisterUserController(userService, bCryptPasswordEncoder);
        webTestClient = WebTestClient.bindToController(registerUserController)
                .configureClient()
                .baseUrl("/api/user")
                .build();

        mockUser = new User();
        mockUser.setUserId("SPM_1");
        mockUser.setUserName("SPM admin");
        mockUser.setUserEmail("spm@test.com");
        mockUser.setPassword("password123");
        mockUser.setActive(true);
        mockUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testRegisterUser_Success() {
        when(userService.createUser(any(User.class))).thenReturn(Mono.just(mockUser));

        webTestClient.post()
                .uri("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                            {
                                "userId": "SPM_1",
                                "userName": "SPM admin",
                                "userEmail": "spm@test.com",
                                 "phoneNumber": "000000000",
                                 "active": true,
                                 "password": "test123"
                            }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.userId").isEqualTo("SPM_1")
                .jsonPath("$.username").isEqualTo("SPM admin")
                .jsonPath("$.userEmail").isEqualTo("spm@test.com");
    }


    @Test
    void testRegisterUser_InvalidJSON() {
        webTestClient.post()
                .uri("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{invalid-json}")
                .exchange()
                .expectStatus().isBadRequest();
    }
}
