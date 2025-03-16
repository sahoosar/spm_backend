package com.spm.portfolio.controller;

import com.spm.portfolio.dto.StockDto;
import com.spm.portfolio.exception.StockNotFoundException;
import com.spm.portfolio.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/av/stock")
@Tag(name = "Alpha Vantage Stock Controller", description = "Fetches real-time stock prices")
@RequiredArgsConstructor
@Slf4j
public class AlphaVantageController {

    private final StockService stockService;

    @GetMapping("/{symbol}")
    @Operation(summary = "Get real-time stock price", description = "Fetches real-time stock price from Alpha Vantage API")
    public Mono<StockDto> getStockPrice(@PathVariable String symbol) {
        return stockService.getRealTimeStockPrice(symbol)
                .doOnNext(stockDto -> log.info(StringUtils.isNoneEmpty(stockDto.getStockSymbol())?" Symbol found "+stockDto.getStockSymbol():"Symbol Not Found !!!")) // Logs the stock symbol
                .switchIfEmpty(Mono.error(new StockNotFoundException("Stock not found for symbol: " + symbol)));
    }
    }

