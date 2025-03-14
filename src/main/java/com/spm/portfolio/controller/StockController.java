package com.spm.portfolio.controller;

import com.spm.portfolio.dto.StockDto;
import com.spm.portfolio.dto.StockResponseDTO;
import com.spm.portfolio.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/stocks")
@Tag(name = "Stock Controller", description = "Fetches real-time stock prices")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/{symbol}")
    @Operation(summary = "Get real-time stock price", description = "Fetches real-time stock price from Alpha Vantage API")
    public Mono<StockDto> getStockPrice(@PathVariable String symbol) {
        System.out.println("Search the stock price::"+symbol);
        return stockService.getRealTimeStockPrice(symbol)
                .switchIfEmpty(Mono.just(new StockDto()));
    }

    // Add stock to a specific list
    @PostMapping
    public String addStock(@RequestParam String symbol) {
       // stockService.addStock(symbol);
        return "Stock added: " + symbol + " at $" ;
    }

}