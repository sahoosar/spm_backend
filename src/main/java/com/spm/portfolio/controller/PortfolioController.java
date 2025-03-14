package com.spm.portfolio.controller;

import com.spm.portfolio.dto.HoldingsDto;
import com.spm.portfolio.model.Portfolio;
import com.spm.portfolio.model.PortfolioSummary;
import com.spm.portfolio.service.UpdateStockService;
import com.spm.portfolio.service.PortfolioStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/portfolio")
@Tag(name = "Portfolio Controller", description = "Manages user portfolio")
public class PortfolioController {
    private final UpdateStockService updateStockService;
    private final PortfolioStatusService portfolioStatusService;


    public PortfolioController(UpdateStockService updateStockService, PortfolioStatusService portfolioStatusService) {
        this.updateStockService = updateStockService;
        this.portfolioStatusService = portfolioStatusService;
    }

    @GetMapping("/daily-summary")
    public Mono<PortfolioSummary> getDailySummary() {
        return portfolioStatusService.getDailySummary();
    }


    @GetMapping
    @Operation(summary = "Get user portfolio", description = "Returns a user holdings")
    public void getPortfolio(ServerHttpRequest request)
    {
        System.out.println("In Portofolio");
    }

    @PostMapping("/stock")
    public Mono<Portfolio> updateStockToPortfolio(@RequestBody HoldingsDto holdingsDto) {
        return updateStockService.upsertStockToPortfolio(holdingsDto);
    }

    @GetMapping("/user/{userId}")
    public Flux<Portfolio> getUserPortfolios(@PathVariable String userId) {
        return updateStockService.getUserPortfolios(userId);
    }
}
