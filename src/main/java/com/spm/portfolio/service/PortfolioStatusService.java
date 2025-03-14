package com.spm.portfolio.service;

import com.spm.portfolio.model.PortfolioSummary;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PortfolioStatusService {
    private final DatabaseClient databaseClient;

    public PortfolioStatusService(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<PortfolioSummary> getDailySummary() {
        String query = """
        SELECT 
            transaction_date,
            SUM(buy_price * quantity) AS total_invested,
            SUM(CASE WHEN profit_loss > 0 THEN profit_loss ELSE 0 END) AS total_profit,
            SUM(CASE WHEN profit_loss < 0 THEN ABS(profit_loss) ELSE 0 END) AS total_loss,
            SUM(profit_loss) AS net_profit_loss,
            COALESCE((SUM(profit_loss) / NULLIF(SUM(buy_price * quantity), 0)) * 100, 0) AS profit_percentage,
            COALESCE((ABS(SUM(profit_loss)) / NULLIF(SUM(buy_price * quantity), 0)) * 100, 0) AS loss_percentage
        FROM portfolio
        WHERE transaction_date = CURDATE()
        GROUP BY transaction_date;
    """;

        return databaseClient.sql(query)
                .map(row -> new PortfolioSummary(
                        row.get("transaction_date", String.class),
                        row.get("total_invested", Double.class),
                        row.get("total_profit", Double.class),
                        row.get("total_loss", Double.class),
                        row.get("net_profit_loss", Double.class),
                        row.get("profit_percentage", Double.class),
                        row.get("loss_percentage", Double.class)
                ))
                .one();
    }
}
