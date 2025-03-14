package com.spm.portfolio.scheduler;

import com.spm.portfolio.service.UpdateStockService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class PortfolioScheduler {

    private final UpdateStockService updateStockService;

    public PortfolioScheduler(UpdateStockService updateStockService) {
        this.updateStockService = updateStockService;
    }

    //@PostConstruct
    public void start() {
        schedulePortfolioUpdateWithRetry(); // Uncomment for production
    }

    // uses boundedElastic scheduler.
    public void schedulePortfolioUpdateWithRetry() {
        schedulePortfolioUpdateWithRetry(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    // Overloaded method for testing – accepts a scheduler.
    public void schedulePortfolioUpdateWithRetry(Scheduler scheduler) {
        Flux.interval(Duration.ofSeconds(30), scheduler)
                .flatMap(tick -> updateStockService.getAllSymbolsFromPortfolio())
                .flatMap(symbol ->
                        updateStockService.updatePortfolio(symbol)
                                // Retry up to 3 times with a 10ms delay using the provided scheduler.
                                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(10)).scheduler(scheduler))
                                .onErrorResume(e -> {
                                    System.err.println("Error updating " + symbol + ": " + e.getMessage());
                                    return Mono.empty();
                                })
                )
                .subscribe();

        Flux.interval(Duration.ofSeconds(30), scheduler)
                .flatMap(tick -> updateStockService.getAllSymbolsFromStockList())
                .flatMap(symbol ->
                        updateStockService.updateStockList(symbol)
                                // Retry up to 3 times with a 10ms delay using the provided scheduler.
                                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(10)).scheduler(scheduler))
                                .onErrorResume(e -> {
                                    System.err.println("Error updating " + symbol + ": " + e.getMessage());
                                    return Mono.empty();
                                })
                )
                .subscribe();

    }

    // used this method to test the retry functionality.
    public void schedulePortfolioUpdateWithRetryForTest(Scheduler scheduler, Duration interval) {
        Flux.interval(interval, Duration.ofMillis(100), scheduler) // initial delay = interval; subsequent emissions every 10ms
                .flatMap(tick -> updateStockService.getAllSymbolsFromPortfolio())
                .flatMap(symbol ->
                        updateStockService.updatePortfolio(symbol)
                                // Retry up to 3 times with a 10ms fixed delay using the provided scheduler.
                                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(10)).scheduler(scheduler))
                                .onErrorResume(e -> {
                                    System.err.println("Error updating " + symbol + ": " + e.getMessage());
                                    return Mono.empty();
                                })
                )
                .subscribe();
    }
}

