package com.spm.portfolio.service;

import com.spm.portfolio.dto.GlobalQuoteDTO;
import com.spm.portfolio.dto.StockDto;
import com.spm.portfolio.dto.StockResponseDTO;
import com.spm.portfolio.exception.StockNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class StockService {
    private final WebClient webClient;
    private final String apiKey;



    public StockService(WebClient webClient, @Value("${webclient.api-key}") String apiKey) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }
    public Mono<StockDto> getRealTimeStockPrice(String symbol) {
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
                .flatMap(stockResponseDTO -> {
                    if (stockResponseDTO == null || stockResponseDTO.getGlobalQuote() == null) {
                        return Mono.error(new StockNotFoundException("No stock data found"));
                    }

                    GlobalQuoteDTO globalQuoteDTO = stockResponseDTO.getGlobalQuote();

                    StockDto stockDto = StockDto.builder()
                            .stockSymbol(globalQuoteDTO.getStockSymbol())
                            .highPrice(parseDoubleSafely(globalQuoteDTO.getHighPrice()))
                            .currentPrice(parseDoubleSafely(globalQuoteDTO.getLatestPrice()))
                            .lowPrice(parseDoubleSafely(globalQuoteDTO.getLowPrice()))
                            .openPrice(parseDoubleSafely(globalQuoteDTO.getOpenPrice()))
                            .build();

                    return Mono.just(stockDto);
                });
    }
    private Double parseDoubleSafely(String value) {
        try {
            return value != null && !value.trim().isEmpty() ? Double.valueOf(value) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0; // Default fallback for invalid values
        }
    }


}
