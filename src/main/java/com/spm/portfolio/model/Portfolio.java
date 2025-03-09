package com.spm.portfolio.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;

@Table("portfolios")
public class Portfolio {
    @Id
    private Long id;
    private String userId;
    private Double totalValue;
    private Instant createdAt;
}
