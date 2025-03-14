package com.spm.portfolio.controller;

import com.spm.portfolio.model.Portfolio;
import com.spm.portfolio.model.PortfolioSummary;
import com.spm.portfolio.service.PortfolioService;
import com.spm.portfolio.service.PortfolioStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/portfolio")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.0.121:4200"},allowCredentials = "true")
@Tag(name = "Portfolio Controller", description = "Manages user portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final PortfolioStatusService portfolioStatusService;


    public PortfolioController(PortfolioService portfolioService, PortfolioStatusService portfolioStatusService) {
        this.portfolioService = portfolioService;
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

    @PostMapping
    public Mono<Portfolio> createPortfolio(@RequestBody Portfolio portfolio) {
        return portfolioService.createPortfolio(portfolio);
    }

    @GetMapping("/user/{userId}")
    public Flux<Portfolio> getUserPortfolios(@PathVariable String userId) {
        return portfolioService.getUserPortfolios(userId);
    }
}
