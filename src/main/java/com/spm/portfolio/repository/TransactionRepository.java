package com.spm.portfolio.repository;

import com.spm.portfolio.model.Transaction;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends R2dbcRepository<Transaction, Long> {
    Flux<Transaction> findByPortfolioId(Long portfolioId);
}

