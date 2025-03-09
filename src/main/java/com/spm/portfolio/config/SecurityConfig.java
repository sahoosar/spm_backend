package com.spm.portfolio.config;

import com.spm.portfolio.service.user.CustomUserDetailsService;
import com.spm.portfolio.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /*@Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()) // ✅ CSRF Token in Cookie
                )
                .authorizeExchange(auth -> auth
                        .pathMatchers("/csrf", "/swagger-ui/**", "/v3/api-docs/**","/stocks").permitAll() // ✅ Allow CSRF token retrieval
                        .anyExchange().authenticated() // Require authentication for other APIs
                )
                .build();
    }*/

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(exchange ->
                        exchange.pathMatchers("/auth/**","/users/**","/swagger-ui/**", "/v3/api-docs/**","/stocks/**").permitAll() // Allow authentication endpoints
                                .anyExchange().authenticated()
                )
                .authenticationManager(reactiveAuthenticationManager())
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return authentication -> {
            String username = authentication.getName();
            String password = authentication.getCredentials().toString();

            return userDetailsService.findByUsername(username)
                    .flatMap(userDetails -> {
                        if (new BCryptPasswordEncoder().matches(password, userDetails.getPassword())) {
                            return Mono.just(new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()));
                        } else {
                            return Mono.empty();
                        }
                    });
        };
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
