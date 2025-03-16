package com.spm.portfolio.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    private JwtUtil jwtUtil;
    private String token;
    private String username = "testUser";

    private final String secret = "spm_secret_key-spm_secret_key-spm_secret_keyspm_secret_keyspm_secret_keyspm_secret_key";

    private final Key SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));//Keys.secretKeyFor(SignatureAlgorithm.HS256);
   // private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        token = jwtUtil.generateToken(username);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        assertNotNull(token, "Generated token should not be null");
        assertTrue(token.length() > 10, "Generated token should have a valid length");
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername, "Extracted username should match the input username");
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        Date expiration = jwtUtil.extractExpiration(token);
        assertNotNull(expiration, "Expiration date should not be null");
        assertTrue(expiration.after(new Date()), "Expiration date should be in the future");
    }

    @Test
    void extractClaim_ShouldReturnValidClaim() {
        Claims claims = jwtUtil.extractClaim(token, c -> c);
        assertNotNull(claims, "Claims should not be null");
        assertEquals(username, claims.getSubject(), "Claims subject should match the input username");
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        assertTrue(jwtUtil.validateToken(token), "Valid token should return true");
    }

    @Test
    void validateToken_ShouldReturnFalseForExpiredToken() throws InterruptedException {
        // Simulating expiration by setting a token with 1-second expiry
        JwtUtil shortExpiryJwtUtil = new JwtUtil() {
            private static final long SHORT_EXPIRATION_TIME = 1000; // 1 second

            @Override
            public String generateToken(String username) {
                return Jwts.builder()
                        .setSubject(username)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + SHORT_EXPIRATION_TIME))
                        .signWith(SECRET_KEY, io.jsonwebtoken.SignatureAlgorithm.HS256)
                        .compact();
            }
        };

        String shortExpiryToken = shortExpiryJwtUtil.generateToken(username);
        Thread.sleep(2000); // Wait for 2 seconds to expire token
        assertFalse(shortExpiryJwtUtil.validateToken(shortExpiryToken), "Expired token should return false");
    }

    @Test
    void validateTokenAndGetUsername_ShouldReturnUsernameForValidToken() {
        String extractedUsername = jwtUtil.validateTokenAndGetUsername(token);
        assertEquals(username, extractedUsername, "Extracted username should match input username");
    }

    @Test
    void validateTokenAndGetUsername_ShouldThrowExceptionForInvalidToken() {
        String invalidToken = token + "invalid"; // Corrupt the token
        assertThrows(Exception.class, () -> jwtUtil.validateTokenAndGetUsername(invalidToken),
                "Invalid token should throw an exception");
    }
}
