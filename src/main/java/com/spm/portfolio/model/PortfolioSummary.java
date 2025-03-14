package com.spm.portfolio.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class PortfolioSummary {
    private String transactionDate;
    private Double totalInvested;
    private Double totalProfit;
    private Double totalLoss;
    private Double netProfitLoss;
    private Double profitPercentage;
    private Double lossPercentage;
}
