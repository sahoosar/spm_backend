package com.spm.portfolio.service;

import com.spm.portfolio.model.StockList;
import com.spm.portfolio.repository.StockListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StockListService {

    private final StockListRepository stockListRepository;
    // Fetch all stocks for a given user
    public Flux<StockList> getStocksByUserId(String userId) {
        return stockListRepository.findByUserId(userId);
    }

    // Add a stock to the list with transaction management
    @Transactional
    public Mono<StockList> addStock(StockList stock) {
        return stockListRepository.save(stock);
    }

    @Transactional
    public Mono<Void> deleteByStockSymbol(String stockName) {
        return stockListRepository.deleteByStockSymbol(stockName);
    }
}
