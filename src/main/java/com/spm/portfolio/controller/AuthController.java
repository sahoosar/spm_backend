package com.spm.portfolio.controller;

import com.spm.portfolio.dto.LoginRequestDto;
import com.spm.portfolio.service.user.CustomUserDetailsService;
import com.spm.portfolio.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;


    @GetMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Boolean> validateToken(@RequestHeader("Authorization") String authHeader) {
        String jwt = "";
        String username;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }
        return Mono.just(jwtUtil.validateToken(jwt));
    }

    @PostMapping("/login")
    public Mono<String> login(@RequestBody LoginRequestDto loginRequestDto, ServerHttpResponse response) {
        return userDetailsService.findByUserId(loginRequestDto.getUserId())
                .flatMap(userDetails -> {
                    if (passwordEncoder.matches(loginRequestDto.getPassword(), userDetails.getPassword())) {
                        String jwtToken = jwtUtil.generateToken(loginRequestDto.getUserId());
                        ResponseCookie cookie = ResponseCookie.from("Bearer", jwtToken)
                                .httpOnly(true)   // Prevent JavaScript access (Security)
                                .secure(true)     // Set to true if using HTTPS
                                .path("/")        // Cookie is available across the site
                                .maxAge(3600)     // 1 hour expiration
                                .sameSite("Strict") // Prevent CSRF attacks
                                .build();
                        ResponseCookie userIdcookie = ResponseCookie.from("userId", loginRequestDto.getUserId())
                                .httpOnly(true)   // Prevent JavaScript access (Security)
                                .secure(true)     // Set to true if using HTTPS
                                .path("/")        // Cookie is available across the site
                                .maxAge(3600)     // 1 hour expiration
                                .sameSite("Strict") // Prevent CSRF attacks
                                .build();
                        response.addCookie(cookie);
                        response.addCookie(userIdcookie);
                        return Mono.just(jwtToken);
                    } else {
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }
                });

    }
}
