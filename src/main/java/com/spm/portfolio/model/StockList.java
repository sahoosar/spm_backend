package com.spm.portfolio.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("stock_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockList {
    @Id
    private Long stockId;

    private String userId;
    private String stockSymbol;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double currentPrice;
    private LocalDateTime createdAt = LocalDateTime.now();
}
