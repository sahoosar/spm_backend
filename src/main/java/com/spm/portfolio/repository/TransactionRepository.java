package com.spm.portfolio.repository;

import com.spm.portfolio.model.TransactionAudit;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface TransactionRepository extends R2dbcRepository<TransactionAudit, Long> {
}

