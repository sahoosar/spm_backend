package com.spm.portfolio.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("portfolios")
@Setter
@Getter
public class Portfolio {
    @Id
    private Long id;
    private String userId;
    private Double totalValue;
    private String symbol;
    private BigDecimal buyPrice;
    private Integer quantity;
    private BigDecimal currentPrice;
    private BigDecimal profitLoss;
}
