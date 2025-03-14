package com.spm.portfolio.scheduler;

import com.spm.portfolio.service.PortfolioService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
public class PortfolioScheduler {
    private final PortfolioService portfolioService;

    public PortfolioScheduler(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    public void schedulePortfolioUpdate() {
        Flux.interval(Duration.ofSeconds(1)) // Run every second
                .flatMap(tick -> portfolioService.getAllSymbols()) // Get all symbols
                .flatMap(symbol -> portfolioService.updatePortfolio(symbol)) // Update each symbol
                .subscribeOn(Schedulers.boundedElastic()) // Run asynchronously
                .subscribe();
    }
}
