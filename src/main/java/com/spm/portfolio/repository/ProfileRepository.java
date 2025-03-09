package com.spm.portfolio.repository;

import com.spm.portfolio.model.Profile;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ProfileRepository extends R2dbcRepository<Profile, Long> {
    Mono<Profile> findByUserId(String userId);
}
