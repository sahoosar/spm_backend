package com.spm.portfolio.model;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Getter
public class PortfolioSummary {

    private String transactionDate;
    private BigDecimal totalInvested;
    private BigDecimal totalProfitLoss;
    private BigDecimal profitLossPercentage;
    private BigDecimal portfolioEvaluation;

}
