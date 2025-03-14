package com.spm.portfolio.service;

import com.spm.portfolio.model.StockList;
import com.spm.portfolio.repository.StockListRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StockListService {

    private final StockListRepository stockListRepository;
    private final DatabaseClient databaseClient; // For transactions

    public StockListService(StockListRepository stockListRepository, DatabaseClient databaseClient) {
        this.stockListRepository = stockListRepository;
        this.databaseClient = databaseClient;
    }

    // Fetch all stocks for a given user
    public Flux<StockList> getStocksByUserId(String userId) {
        return stockListRepository.findByUserId(userId);
    }

    // Add a stock to the list with transaction management
    @Transactional
    public Mono<StockList> addStock(StockList stock) {
        return stockListRepository.save(stock);
    }

    // Delete a stock by ID
    @Transactional
    public Mono<Void> deleteStock(Long stockId) {
        return stockListRepository.deleteById(stockId);
    }

    @Transactional
    public Mono<Void> deleteByStockSymbol(String stockName) {
        return stockListRepository.deleteByStockSymbol(stockName);
    }
}
