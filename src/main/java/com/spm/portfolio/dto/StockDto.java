package com.spm.portfolio.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDto {

    private String stockSymbol;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double currentPrice;
    private LocalDateTime createdAt = LocalDateTime.now();
}
