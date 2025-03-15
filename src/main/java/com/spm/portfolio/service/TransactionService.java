package com.spm.portfolio.service;

import com.spm.portfolio.model.TransactionAudit;
import com.spm.portfolio.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Transactional
    public Mono<TransactionAudit> saveTransaction(TransactionAudit transaction) {
        log.info("Saving transaction for Stock {}", transaction.getStockSymbol());
        return transactionRepository.save(transaction)
                .doOnError(error -> log.error("Error saving transaction", error));
    }

}
