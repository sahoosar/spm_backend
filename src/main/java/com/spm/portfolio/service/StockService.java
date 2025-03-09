package com.spm.portfolio.service;

import com.spm.portfolio.dto.StockResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class StockService {
    private final WebClient webClient;
    private final String apiKey;

    private final CsrfService csrfService; // Inject CSRF service


    public StockService(WebClient webClient, @Value("${webclient.api-key}") String apiKey, CsrfService csrfService) {
        this.webClient = webClient;
        this.apiKey = apiKey;
        this.csrfService = csrfService;
    }
    public Mono<StockResponseDTO> getRealTimeStockPrice(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("function", "GLOBAL_QUOTE")
                        .queryParam("symbol", symbol)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                        response.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(
                                new RuntimeException("Client Error: " + response.statusCode() + " - " + errorBody)
                        )))
                .onStatus(status -> status.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new RuntimeException("Server Error: " + response.statusCode() + " - " + errorBody)
                                )))
                .bodyToMono(StockResponseDTO.class)
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.error(new RuntimeException("API Error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString())))
                .onErrorResume(Exception.class, ex ->
                        Mono.error(new RuntimeException("Unexpected Error: " + ex.getMessage())));
    }


    public Mono<String> addStock(String symbol) {
        return csrfService.getCsrfToken().flatMap(csrfToken -> // ✅ Fetch CSRF Token first
                webClient.post()
                        .uri("/stocks/add")
                        .header("X-CSRF-TOKEN", csrfToken) // ✅ Add CSRF token in header
                        .bodyValue("symbol:"+symbol)
                        .retrieve()
                        .bodyToMono(String.class)
        );
    }
}
