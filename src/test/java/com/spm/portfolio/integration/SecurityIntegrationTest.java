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


    private String getValidToken() {
        return "Bearer " + jwtUtil.generateToken("user123");
    }

    @Test
    public void testPublicEndpointWithDummyToken() {
        // For this test, stub jwtUtil.validateToken or assume that /auth/token simply returns a Boolean.
        // Here, we just send a dummy token.
        webTestClient.get()
                .uri("/api/auth/token")
                .header("Authorization", "Bearer dummy-token")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void testPublicEndpointWithToken() {
        // For this test, stub jwtUtil.validateToken or assume that /auth/token simply returns a Boolean.
        // Here, we just send a dummy token.
        String token = getValidToken();
        webTestClient.get()
                .uri("/api/auth/token")
                .header("Authorization", token)
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

}
