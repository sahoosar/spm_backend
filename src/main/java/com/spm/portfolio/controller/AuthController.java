package com.spm.portfolio.controller;

import com.spm.portfolio.dto.TokenDto;
import com.spm.portfolio.model.User;
import com.spm.portfolio.repository.UserRepository;
import com.spm.portfolio.service.user.CustomUserDetailsService;
import com.spm.portfolio.util.JwtUtil;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil,
                          UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/token")
    public boolean validateToken(@RequestHeader("Authorization") String authHeader)
    {
       return  jwtUtil.validateToken(authHeader);

    }

    @PostMapping("/login")
    public Mono<String> login(@RequestBody User user, ServerHttpResponse response) {
        return userDetailsService.findByUserId(user.getUserId())
                .flatMap(userDetails -> {
                    if (passwordEncoder.matches(user.getPassword(), userDetails.getPassword())) {
                        String jwtToken = jwtUtil.generateToken(user.getUserId());
                        ResponseCookie cookie = ResponseCookie.from("Bearer", jwtToken)
                                .httpOnly(true)   // Prevent JavaScript access (Security)
                                .secure(true)     // Set to true if using HTTPS
                                .path("/")        // Cookie is available across the site
                                .maxAge(3600)     // 1 hour expiration
                                .sameSite("Strict") // Prevent CSRF attacks
                                .build();
                        response.addCookie(cookie);
                        return Mono.just(jwtToken);
                    } else {
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }
                });

    }
}
