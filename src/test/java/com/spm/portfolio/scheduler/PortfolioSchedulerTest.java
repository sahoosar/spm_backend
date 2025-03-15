package com.spm.portfolio.scheduler;

import com.spm.portfolio.service.UpdateStockService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import reactor.test.scheduler.VirtualTimeScheduler;


import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PortfolioSchedulerTest {

    private UpdateStockService updateStockService;
    private PortfolioScheduler portfolioScheduler;

    @BeforeEach
    public void setUp() {
        // Create a mock PortfolioService
        updateStockService = mock(UpdateStockService.class);
        portfolioScheduler = new PortfolioScheduler(updateStockService);
    }

    @Test
    public void testSchedulePortfolioUpdateWithVirtualTime() {
        // Arrange: Stub the service methods to return valid publishers.
        when(updateStockService.getAllSymbolsFromPortfolio()).thenReturn(Flux.just("AAPL", "GOOGL"));
        when(updateStockService.updatePortfolioCurrentPrice(anyString())).thenReturn(Mono.empty());

        // Set up virtual time scheduler
        VirtualTimeScheduler virtualTimeScheduler = VirtualTimeScheduler.getOrSet();

        // Act: Schedule the portfolio update.
        portfolioScheduler.schedulePortfolioUpdateWithRetry(virtualTimeScheduler);

        // Advance virtual time by 11 seconds so that at least one tick is emitted.
        virtualTimeScheduler.advanceTimeBy(Duration.ofSeconds(11));

        // Assert: Verify that the service methods are invoked.
        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(updateStockService, atLeastOnce()).getAllSymbolsFromPortfolio();
                    verify(updateStockService, atLeastOnce()).updatePortfolioCurrentPrice(anyString());
                });
    }

    @Test
    public void testSchedulerHandlesErrorsWithOnErrorContinue() {
        // Arrange: Stub getAllSymbols() to emit three symbols.
        when(updateStockService.getAllSymbolsFromPortfolio())
                .thenReturn(Flux.just("AAPL", "MSFT", "GOOGL"));

        // Stub updatePortfolio for "AAPL" and "GOOGL" to return Mono.empty()
        when(updateStockService.updatePortfolioCurrentPrice("AAPL")).thenReturn(Mono.empty());
        when(updateStockService.updatePortfolioCurrentPrice("GOOGL")).thenReturn(Mono.empty());
        // For "MSFT", simulate an error.
        when(updateStockService.updatePortfolioCurrentPrice("MSFT")).thenReturn(Mono.error(new RuntimeException("Error updating MSFT")));

        // Use a VirtualTimeScheduler to simulate the passage of time.
        VirtualTimeScheduler virtualTimeScheduler = VirtualTimeScheduler.getOrSet();

        // Act: Schedule the portfolio update.
        portfolioScheduler.schedulePortfolioUpdateWithRetry(virtualTimeScheduler);
        // Advance virtual time by 11 seconds so at least one tick occurs.
        virtualTimeScheduler.advanceTimeBy(Duration.ofSeconds(11));

        // Awaitility can be used to wait until our service methods have been invoked.
        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    // Verify that getAllSymbols() was called at least once.
                    verify(updateStockService, atLeastOnce()).getAllSymbolsFromPortfolio();
                    // Verify that updatePortfolio() was called for each symbol.
                    verify(updateStockService, atLeastOnce()).updatePortfolioCurrentPrice("AAPL");
                    verify(updateStockService, atLeastOnce()).updatePortfolioCurrentPrice("GOOGL");
                    verify(updateStockService, atLeastOnce()).updatePortfolioCurrentPrice("MSFT");
                });
    }


    @Test
    public void testSchedulerWithRetryOption_ImmediateInterval() {
        // Arrange: Stub getAllSymbols() to return three symbols.
        when(updateStockService.getAllSymbolsFromPortfolio())
                .thenReturn(Flux.just("AAPL", "MSFT", "GOOGL"));
        when(updateStockService.updatePortfolioCurrentPrice("AAPL")).thenReturn(Mono.empty());
        when(updateStockService.updatePortfolioCurrentPrice("GOOGL")).thenReturn(Mono.empty());

        // For "MSFT", simulate two failures then a success.
        AtomicInteger msftCounter = new AtomicInteger(0);
        when(updateStockService.updatePortfolioCurrentPrice("MSFT")).thenAnswer(invocation -> {
            int count = msftCounter.incrementAndGet();
            if (count < 3) {
                return Mono.error(new RuntimeException("Error updating MSFT"));
            } else {
                return Mono.empty();
            }
        });

        // Set up VirtualTimeScheduler.
        VirtualTimeScheduler virtualTimeScheduler = VirtualTimeScheduler.getOrSet();

        // Act: Use the test-specific scheduler method with an immediate interval.
        portfolioScheduler.schedulePortfolioUpdateWithRetryForTest(virtualTimeScheduler, Duration.ZERO);

        // Advance virtual time sufficiently: give enough time for the immediate tick and retry delays.
        virtualTimeScheduler.advanceTimeBy(Duration.ofMillis(200));

        // Assert: Verify that updatePortfolio("MSFT") was called at least 3 times.
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(updateStockService, atLeast(3)).updatePortfolioCurrentPrice("MSFT");
                    verify(updateStockService, atLeastOnce()).getAllSymbolsFromPortfolio();
                });
    }
}

