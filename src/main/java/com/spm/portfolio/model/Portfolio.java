package com.spm.portfolio.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("portfolios")
@Setter
@Getter
@Builder
public class Portfolio {
    @Id
    private Long portfolioId;
    private String userId;
    private BigDecimal totalValue;
    private String stockSymbol;
    private BigDecimal buyPrice;
    private Integer quantity;
    private BigDecimal currentPrice;
    private BigDecimal profitLoss;
}
