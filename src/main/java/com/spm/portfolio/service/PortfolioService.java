package com.spm.portfolio.service;

import com.spm.portfolio.model.Portfolio;
import com.spm.portfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Transactional
    public Mono<Portfolio> createPortfolio(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }

    public Flux<Portfolio> getUserPortfolios(String userId) {
        return portfolioRepository.findByUserId(userId);
    }
}
