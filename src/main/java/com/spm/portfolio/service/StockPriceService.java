package com.spm.portfolio.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class StockPriceService {
    private final WebClient webClient;

    public StockPriceService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<BigDecimal> getStockPrice(String symbol) {
        return webClient.get()
                .uri("/stock-price?symbol=" + symbol)
                .retrieve()
                .bodyToMono(String.class)
                .map(price -> new BigDecimal(price)); // Convert String to BigDecimal
    }
}