package com.spm.portfolio.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HoldingsDto {
    private String userId;
    private String symbol;
    private BigDecimal price;
    private Integer quantity;
    private String operation; // for buy or sell operation
}
