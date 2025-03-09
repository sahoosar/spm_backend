package com.spm.portfolio.repository;

import com.spm.portfolio.model.UserRole;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface UserRoleRepository extends R2dbcRepository<UserRole, String> {
    Flux<UserRole> findByUserId(String userId);
}
