package com.spm.portfolio.config;

import com.spm.portfolio.repository.JwtSecurityContextRepository;
import com.spm.portfolio.service.user.CustomUserDetailsService;
import com.spm.portfolio.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

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
                //.cors(ServerHttpSecurity.CorsSpec::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:4200")); // Allow UI URL
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .authorizeExchange(exchange ->
                        exchange.pathMatchers(
                                "/swagger-ui/**",
                                        "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/api-docs/**",
                                        "/auth/**", "/users/**"
                        ).permitAll()
                        .pathMatchers("/stocks/**","/api/stocks/**")
                                .authenticated()
                ).authenticationManager(reactiveAuthenticationManager())

                .securityContextRepository(new JwtSecurityContextRepository(jwtUtil)) // Store authentication state

                .build();
    }

    /*@Bean
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
    }*/
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return authentication -> {
            String token = authentication.getCredentials().toString();
            String username = jwtUtil.extractUsername(token);

            if (jwtUtil.validateToken(token)) {
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                return Mono.just(new UsernamePasswordAuthenticationToken(username, null, authorities));
            } else {
                return Mono.empty();
            }
        };
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
