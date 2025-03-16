package com.spm.portfolio.service;

import com.spm.portfolio.model.PortfolioSummary;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PortfolioSummaryService {
    private final DatabaseClient databaseClient;

    public PortfolioSummaryService(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<PortfolioSummary> getDailySummary(String userId) {
        String query = """
                    SELECT  SUM(total_value) AS total_invested , 
                    SUM( current_price  * quantity) as portfolio_evaluation,
                     SUM(profit_loss) as total_profit_loss,COALESCE((SUM(profit_loss) /
                      NULLIF(SUM(buy_price * quantity), 0)) * 100, 0) AS profit_loss_percentage from portfolios where user_id= ?;
                """;

        return databaseClient
                .sql(query)
                .bind(0,userId)
                .map(row ->
                    PortfolioSummary.builder().
                            transactionDate(LocalDateTime.now().toString())
                            .portfolioEvaluation(row.get("portfolio_evaluation", BigDecimal.class))
                            .profitLossPercentage(row.get("profit_loss_percentage", BigDecimal.class))
                            .totalInvested(row.get("total_invested", BigDecimal.class))
                            .totalProfitLoss(row.get("total_profit_loss", BigDecimal.class))
                            .build()
                ).one();
    }
}
