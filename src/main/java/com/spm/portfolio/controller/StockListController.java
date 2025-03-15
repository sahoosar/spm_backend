package com.spm.portfolio.controller;

import com.spm.portfolio.model.StockList;
import com.spm.portfolio.service.StockListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/stocksList")
@RequiredArgsConstructor
public class StockListController {

    private final StockListService stockListService;


    //  Add a new stock to the list.
    @PostMapping
    public Mono<ResponseEntity<StockList>> addStock(@RequestBody StockList stock) {
        return stockListService.addStock(stock)
                .map(savedStock -> ResponseEntity.ok(savedStock));
    }

    // Delete a stock from the list.
    @DeleteMapping("/{stockSymbol}")
    public Mono<ResponseEntity<Void>> deleteStock(@PathVariable String stockSymbol) {
        return stockListService.deleteByStockSymbol(stockSymbol)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    // Fetch all stocks for a given user.
    @GetMapping("/users/{userId}")
    public Flux<StockList> getStocksByUser(@PathVariable String userId) {
        return stockListService.getStocksByUserId(userId);
    }

}
