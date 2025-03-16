package com.spm.portfolio.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class JwtAuthenticationWebFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private WebFilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    private static final String VALID_TOKEN = "valid-token";
    private static final String INVALID_TOKEN = "invalid-token";
    private static final String USERNAME = "testUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());
    }

    @Test
    void filter_ShouldAuthenticateUser_WhenValidTokenIsProvided() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + VALID_TOKEN);

        when(request.getHeaders()).thenReturn(headers);
        when(jwtUtil.validateTokenAndGetUsername(VALID_TOKEN)).thenReturn(USERNAME);

        Mono<Void> result = jwtAuthenticationWebFilter.filter(exchange, filterChain);

        StepVerifier.create(result)
                .verifyComplete();

        verify(filterChain, times(1)).filter(exchange);
    }

    @Test
    void filter_ShouldReturnUnauthorized_WhenInvalidTokenIsProvided() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + INVALID_TOKEN);

        when(request.getHeaders()).thenReturn(headers);
        when(jwtUtil.validateTokenAndGetUsername(INVALID_TOKEN))
                .thenThrow(new RuntimeException("Invalid Token"));

        doAnswer(invocation -> {
            return null; // No return value, just mocking behavior
        }).when(response).setStatusCode(HttpStatus.UNAUTHORIZED);

        when(response.setComplete()).thenReturn(Mono.empty());

        Mono<Void> result = jwtAuthenticationWebFilter.filter(exchange, filterChain);

        StepVerifier.create(result)
                .verifyComplete();

        verify(response, times(1)).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(response, times(1)).setComplete();
    }

    @Test
    void filter_ShouldProceed_WhenNoAuthorizationHeaderIsPresent() {
        HttpHeaders headers = new HttpHeaders();

        when(request.getHeaders()).thenReturn(headers);

        Mono<Void> result = jwtAuthenticationWebFilter.filter(exchange, filterChain);

        StepVerifier.create(result)
                .verifyComplete();

        verify(filterChain, times(1)).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }
}
