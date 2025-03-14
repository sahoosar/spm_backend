package com.spm.portfolio.repository;
import com.spm.portfolio.model.Portfolio;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PortfolioRepository extends R2dbcRepository<Portfolio, Long> {
    Mono<Portfolio> findBySymbol(String symbol);
}
