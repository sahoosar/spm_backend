package com.spm.portfolio.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("transactions")
public class Transaction {
    @Id
    private Long id;
    private Long portfolioId;
    private String stockSymbol;
    private String transactionType;
    private int quantity;
    private Double price;
    private LocalDateTime transactionDate;
}
