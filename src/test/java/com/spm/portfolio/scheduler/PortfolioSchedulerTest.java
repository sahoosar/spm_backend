package com.spm.portfolio.scheduler;

import com.spm.portfolio.service.UpdateStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.scheduler.VirtualTimeScheduler;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PortfolioSchedulerTest {

   @Mock
   private UpdateStockService updateStockService;

    @InjectMocks
    private PortfolioScheduler portfolioScheduler;

    private VirtualTimeScheduler testScheduler;

    @BeforeEach
    public void setUp() {
        updateStockService = mock(UpdateStockService.class);
        portfolioScheduler = new PortfolioScheduler(updateStockService);

        // Ensure fresh values are emitted for each subscription
        when(updateStockService.getAllSymbolsFromPortfolio())
                .thenReturn(Flux.defer(() -> Flux.just("AAPL", "GOOGL")));

        when(updateStockService.getAllSymbolsFromStockList())
                .thenReturn(Flux.defer(() -> Flux.just("TSLA", "AMZN")));
    }

    @Test
    public void testSchedulePortfolioUpdateWithVirtualTime() {
        // Arrange
        when(updateStockService.getAllSymbolsFromPortfolio())
                .thenReturn(Flux.defer(() -> Flux.just("AAPL", "GOOGL")));
        when(updateStockService.updatePortfolioCurrentPrice(anyString())).thenReturn(Mono.empty());

        VirtualTimeScheduler virtualTimeScheduler = VirtualTimeScheduler.getOrSet();

        portfolioScheduler.schedulePortfolioUpdateWithRetry(virtualTimeScheduler);

        // Ensure enough time for at least one execution cycle
        virtualTimeScheduler.advanceTimeBy(Duration.ofSeconds(31));

        // Assert
        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(updateStockService, atLeastOnce()).getAllSymbolsFromPortfolio();
                    verify(updateStockService, atLeastOnce()).updatePortfolioCurrentPrice(anyString());
                });
    }

    @Test
    public void testSchedulerHandlesErrorsWithOnErrorContinue() {
        // Arrange
        when(updateStockService.getAllSymbolsFromPortfolio())
                .thenReturn(Flux.defer(() -> Flux.just("AAPL", "MSFT", "GOOGL")));

        when(updateStockService.updatePortfolioCurrentPrice("AAPL")).thenReturn(Mono.empty());
        when(updateStockService.updatePortfolioCurrentPrice("GOOGL")).thenReturn(Mono.empty());

        // Simulate an error for "MSFT"
        when(updateStockService.updatePortfolioCurrentPrice("MSFT"))
                .thenReturn(Mono.error(new RuntimeException("Error updating MSFT")));

        VirtualTimeScheduler virtualTimeScheduler = VirtualTimeScheduler.getOrSet();

        portfolioScheduler.schedulePortfolioUpdateWithRetry(virtualTimeScheduler);

        // Ensure enough time for at least one execution cycle
        virtualTimeScheduler.advanceTimeBy(Duration.ofSeconds(31));

        // Assert
        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(updateStockService, atLeastOnce()).getAllSymbolsFromPortfolio();
                    verify(updateStockService, atLeastOnce()).updatePortfolioCurrentPrice("AAPL");
                    verify(updateStockService, atLeastOnce()).updatePortfolioCurrentPrice("GOOGL");
                    verify(updateStockService, atLeastOnce()).updatePortfolioCurrentPrice("MSFT");
                });
    }
    @Test
    public void testSchedulerWithRetryOption_ImmediateInterval() {
        // Arrange
        when(updateStockService.getAllSymbolsFromPortfolio())
                .thenReturn(Flux.defer(() -> Flux.just("AAPL", "MSFT", "GOOGL")));
        when(updateStockService.updatePortfolioCurrentPrice("AAPL")).thenReturn(Mono.empty());
        when(updateStockService.updatePortfolioCurrentPrice("GOOGL")).thenReturn(Mono.empty());

        // Simulate MSFT failing twice before succeeding
        AtomicInteger msftCounter = new AtomicInteger(0);
        when(updateStockService.updatePortfolioCurrentPrice("MSFT")).thenAnswer(invocation -> {
            if (msftCounter.incrementAndGet() < 3) {
                return Mono.error(new RuntimeException("Error updating MSFT"));
            }
            return Mono.empty();
        });

        VirtualTimeScheduler virtualTimeScheduler = VirtualTimeScheduler.getOrSet();

        portfolioScheduler.schedulePortfolioUpdateWithRetryForTest(virtualTimeScheduler, Duration.ofMillis(10));

        // Ensure enough time passes for retries
        virtualTimeScheduler.advanceTimeBy(Duration.ofMillis(500));

        // Assert
        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(updateStockService, atLeast(3)).updatePortfolioCurrentPrice("MSFT");
                    verify(updateStockService, atLeastOnce()).getAllSymbolsFromPortfolio();
                });
    }

}

