package com.spm.portfolio.integration;

import com.spm.portfolio.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class SecurityIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Test that public endpoints (e.g. /auth/token) are accessible without authentication.
     */
    @Test
    public void testPublicEndpointAccess() {
        // For this test, stub jwtUtil.validateToken or assume that /auth/token simply returns a Boolean.
        // Here, we just send a dummy token.
        webTestClient.get()
                .uri("/auth/token")
                .header("Authorization", "dummy-token")
                .exchange()
                .expectStatus().isOk();
    }

    /**
     * Test that protected endpoints return 401 (Unauthorized) when no token is provided.
     */
    @Test
    public void testProtectedEndpointAccessWithoutAuth() {
        // Protected endpoint under /api/stocks/** should be secured.
        webTestClient.get()
                .uri("/api/stocks/users/user123")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    /**
     * Test that protected endpoints are accessible when a valid token is provided.
     */
    @Test
    public void testProtectedEndpointAccessWithValidToken() {
        // Assume jwtUtil.generateToken("user123") returns a valid JWT token recognized by your security configuration.
        String rawToken = jwtUtil.generateToken("user123");
        String token = "Bearer " + rawToken;  // Prepend the Bearer prefix

        webTestClient.get()
                .uri("/api/stocks/users/user123")
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk();
    }
}
