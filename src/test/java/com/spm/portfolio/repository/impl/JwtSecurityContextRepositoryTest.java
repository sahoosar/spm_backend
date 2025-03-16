package com.spm.portfolio.repository.impl;

import com.spm.portfolio.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

    class JwtSecurityContextRepositoryTest {

        @Mock
        private JwtUtil jwtUtil;

        @Mock
        private ServerWebExchange exchange;

        @Mock
        private org.springframework.http.server.reactive.ServerHttpRequest request;

        @InjectMocks
        private JwtSecurityContextRepository securityContextRepository;

        private static final String VALID_TOKEN = "valid-token";
        private static final String INVALID_TOKEN = "invalid-token";
        private static final String USERNAME = "testUser";

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            when(exchange.getRequest()).thenReturn(request);
        }

        @Test
        void load_ShouldReturnSecurityContext_WhenValidTokenIsProvided() {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + VALID_TOKEN);

            when(request.getHeaders()).thenReturn(headers);
            when(jwtUtil.validateToken(VALID_TOKEN)).thenReturn(true);
            when(jwtUtil.extractUsername(VALID_TOKEN)).thenReturn(USERNAME);

            Mono<SecurityContext> result = securityContextRepository.load(exchange);

            StepVerifier.create(result)
                    .expectNextMatches(context -> {
                        UsernamePasswordAuthenticationToken auth =
                                (UsernamePasswordAuthenticationToken) context.getAuthentication();
                        return auth.getName().equals(USERNAME) && auth.getAuthorities().size() == 1;
                    })
                    .verifyComplete();
        }

        @Test
        void load_ShouldReturnEmptyMono_WhenNoAuthorizationHeaderPresent() {
            HttpHeaders headers = new HttpHeaders();

            when(request.getHeaders()).thenReturn(headers);

            Mono<SecurityContext> result = securityContextRepository.load(exchange);

            StepVerifier.create(result)
                    .verifyComplete();

            verify(jwtUtil, never()).validateToken(any());
        }

        @Test
        void load_ShouldReturnEmptyMono_WhenInvalidTokenIsProvided() {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + INVALID_TOKEN);

            when(request.getHeaders()).thenReturn(headers);
            when(jwtUtil.validateToken(INVALID_TOKEN)).thenReturn(false);

            Mono<SecurityContext> result = securityContextRepository.load(exchange);

            StepVerifier.create(result)
                    .verifyComplete();

            verify(jwtUtil, times(1)).validateToken(INVALID_TOKEN);
            verify(jwtUtil, never()).extractUsername(any());
        }

        @Test
        void save_ShouldReturnEmptyMono() {
            Mono<Void> result = securityContextRepository.save(exchange, new SecurityContextImpl(null));

            StepVerifier.create(result)
                    .verifyComplete();
        }
    }
