package com.spm.portfolio.service;

import com.spm.portfolio.dto.HoldingsDto;
import com.spm.portfolio.model.Portfolio;
import com.spm.portfolio.model.StockList;
import com.spm.portfolio.model.TransactionAudit;
import com.spm.portfolio.repository.PortfolioRepository;
import com.spm.portfolio.repository.StockListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateStockService {

    private final PortfolioRepository portfolioRepository;
    private final StockService stockService;
    private final TransactionalOperator transactionalOperator;
    private final StockListRepository stockListRepository;

    private final TransactionService transactionService;

    // Fetch all stock symbols Avoid duplicates
    public Flux<String> getAllSymbolsFromPortfolio() {
        return portfolioRepository.findAll()
                .map(Portfolio::getStockSymbol) // Extract symbols
                .distinct();
    }

    public Flux<String> getAllSymbolsFromStockList() {
        return stockListRepository.findAll()
                .map(StockList::getStockSymbol) // Extract symbols
                .distinct();
    }
    public Mono<Void> updatePortfolioCurrentPrice(String symbol) {
        return portfolioRepository.findByStockSymbol(symbol)
                .switchIfEmpty(Mono.error(new RuntimeException("Symbol not found: " + symbol))) // Ensure symbol exists
                .flatMap(portfolio -> stockService.getRealTimeStockPrice(symbol)
                        .flatMap(stockDto -> {
                            BigDecimal profitLoss = BigDecimal.valueOf(portfolio.getQuantity())
                                    .multiply(BigDecimal.valueOf(stockDto.getCurrentPrice()).subtract(portfolio.getBuyPrice()));
                            portfolio.setCurrentPrice(BigDecimal.valueOf(stockDto.getCurrentPrice()));
                            portfolio.setProfitLoss(profitLoss);

                            return portfolioRepository.save(portfolio);
                        })
                        .onErrorResume(error -> {
                            log.error("Error fetching stock price: " + error.getMessage());
                            return Mono.empty(); // Skip saving if stock price fails
                        }))
                .as(transactionalOperator::transactional) // Ensure transaction safety
                .then();
    }

    public Mono<Void> updateStockListCurrentPrice(String symbol) {
        return stockListRepository.findByStockSymbol(symbol)
                .switchIfEmpty(Mono.error(new RuntimeException("Symbol not found: " + symbol))) // Ensure symbol exists
                .flatMap(stock -> stockService.getRealTimeStockPrice(symbol)
                        .flatMap(stockDto -> {
                            stock.setCurrentPrice(stockDto.getCurrentPrice());
                            return stockListRepository.save(stock);
                        })
                        .onErrorResume(error -> {
                            System.err.println("Error fetching stock price: " + error.getMessage());
                            return Mono.empty(); // Skip saving if stock price fails
                        }))
                .as(transactionalOperator::transactional) // Ensure transaction safety
                .then();
    }

    public Mono<Object> upsertStockToPortfolio(HoldingsDto holdingsDto) {

        AtomicReference<Boolean> isStockDeleted = new AtomicReference<>(false);
        Mono<Portfolio> portfolioStock = portfolioRepository.getPortfolioStockByUserIdAndStockSymbol(holdingsDto.getUserId(), holdingsDto.getSymbol());
        return portfolioStock.flatMap(stock -> {

            // Perform some update logic
            if (holdingsDto.getOperation().equalsIgnoreCase("buy")) {
                stock.setQuantity(stock.getQuantity() + holdingsDto.getQuantity());
                stock.setTotalValue(stock.getTotalValue().add(BigDecimal.valueOf(holdingsDto.getQuantity())
                        .multiply(holdingsDto.getPrice())));
            } else {
                stock.setQuantity(stock.getQuantity() - holdingsDto.getQuantity());
                stock.setTotalValue(stock.getTotalValue().subtract(BigDecimal.valueOf(holdingsDto.getQuantity())
                        .multiply(holdingsDto.getPrice())));
            }
            if (stock.getQuantity() > 0) {
                stock.setBuyPrice(stock.getTotalValue().divide(
                        BigDecimal.valueOf(stock.getQuantity()), 2, RoundingMode.HALF_UP //  Specify Rounding Mode
                ));
                saveToAudit(holdingsDto);
                return portfolioRepository.save(stock);
            } else {
                isStockDeleted.set(true);
                saveToAudit(holdingsDto);
                return portfolioRepository.delete(stock);
            }

        }).switchIfEmpty(Mono.defer(() -> {
            if (isStockDeleted.get() || holdingsDto.getOperation().equalsIgnoreCase("sell")) {
                return Mono.empty();
            } else {
                return addStockToHoldings(holdingsDto);
            }
        }));
    }

    private Mono<Portfolio> addStockToHoldings(HoldingsDto holdingsDto) {
        Portfolio newStock = Portfolio.builder()
                .userId(holdingsDto.getUserId())
                .buyPrice(holdingsDto.getPrice())
                .quantity(holdingsDto.getQuantity())
                .stockSymbol(holdingsDto.getSymbol())
                .totalValue(BigDecimal.valueOf(holdingsDto.getQuantity())
                        .multiply(holdingsDto.getPrice()))
                .build();
        saveToAudit(holdingsDto);
        return portfolioRepository.save(newStock);
    }

    private void saveToAudit(HoldingsDto holdingsDto) {
        transactionService.saveTransaction(TransactionAudit.builder()
                        .operationType(holdingsDto.getOperation())
                        .stockSymbol(holdingsDto.getSymbol())
                        .userId(holdingsDto.getUserId())
                        .quantity(holdingsDto.getQuantity())
                        .price(holdingsDto.getPrice())
                        .transactionDate(LocalDateTime.now()).build())
                .subscribe(
                        saved -> log.info("Transaction saved into table :{} ", saved.getTransactionId()),
                        error -> log.error("Error saving transaction: {}", error)
                );
    }

    public Flux<Portfolio> getUserPortfolios(String userId) {
        return portfolioRepository.findAllByUserId(userId);
    }
}
