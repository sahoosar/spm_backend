package com.spm.portfolio.service;

import com.spm.portfolio.model.Portfolio;
import com.spm.portfolio.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final StockPriceService stockPriceService;
    private final TransactionalOperator transactionalOperator;

    // Fetch all stock symbols Avoid duplicates
    public Flux<String> getAllSymbols() {
        return portfolioRepository.findAll()
                .map(Portfolio::getSymbol) // Extract symbols
                .distinct();
    }

    public Mono<Void> updatePortfolio(String symbol) {
        return portfolioRepository.findBySymbol(symbol)
                .switchIfEmpty(Mono.error(new RuntimeException("Symbol not found: " + symbol))) // Ensure symbol exists
                .flatMap(portfolio -> stockPriceService.getStockPrice(symbol)
                        .flatMap(currentPrice -> {
                            BigDecimal profitLoss = BigDecimal.valueOf(portfolio.getQuantity())
                                    .multiply(currentPrice.subtract(portfolio.getBuyPrice()));

                            portfolio.setCurrentPrice(currentPrice);
                            portfolio.setProfitLoss(profitLoss);

                            return portfolioRepository.save(portfolio);
                        })
                        .onErrorResume(error -> {
                            System.err.println("Error fetching stock price: " + error.getMessage());
                            return Mono.empty(); // Skip saving if stock price fails
                        }))
                .as(transactionalOperator::transactional) // Ensure transaction safety
                .then();
    }


    public Mono<Portfolio> createPortfolio(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }

   public Flux<Portfolio> getUserPortfolios(String userId) {
        return null;
    }
}
