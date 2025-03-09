package com.spm.portfolio.controller;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/csrf")
public class CsrfController {

    @GetMapping
    public Mono<Map<String, String>> getCsrfToken(ServerWebExchange exchange) {
        return exchange.getAttributeOrDefault(CsrfToken.class.getName(), Mono.empty())
                .cast(CsrfToken.class)
                .map(csrfToken -> Map.of(
                        "headerName", csrfToken.getHeaderName(),
                        "token", csrfToken.getToken()
                ))
                .defaultIfEmpty(Map.of("error", "CSRF token not found"));
    }
}
