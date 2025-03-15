package com.spm.portfolio.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("transactions_audit")
@Builder
@Setter
@Getter
public class TransactionAudit {
    @Id
    private Long transactionId;
    private String userId;
    private String stockSymbol;
    private String operationType;
    private int quantity;
    private BigDecimal price;
    private LocalDateTime transactionDate;
}
