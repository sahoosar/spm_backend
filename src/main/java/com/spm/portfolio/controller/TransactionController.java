package com.spm.portfolio.controller;

import com.spm.portfolio.model.Transaction;
import com.spm.portfolio.service.TransactionService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public Mono<Transaction> saveTransaction(@RequestBody Transaction transaction) {
        return transactionService.saveTransaction(transaction);
    }

    @GetMapping("/portfolio/{portfolioId}")
    public Flux<Transaction> getTransactionsByPortfolio(@PathVariable Long portfolioId) {
        return transactionService.getTransactionsByPortfolio(portfolioId);
    }
}
