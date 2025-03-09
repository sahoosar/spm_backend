package com.spm.portfolio.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CsrfService {

        private final Mono<String> cachedCsrfToken;

        public CsrfService() {
            this.cachedCsrfToken = generateCsrfToken().cache(Duration.ofMinutes(10)); // ✅ Cache token for 10 minutes
        }

        private Mono<String> generateCsrfToken() {
            return Mono.fromSupplier(() -> {
                SecureRandom secureRandom = new SecureRandom();
                byte[] randomBytes = new byte[32];
                secureRandom.nextBytes(randomBytes);
                String csrfToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
                System.out.println("Generating new CSRF token: " + csrfToken);
                return csrfToken;
            });
        }

        public Mono<String> getCsrfToken() {
            return cachedCsrfToken;
        }

}
