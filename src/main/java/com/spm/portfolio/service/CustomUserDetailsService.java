package com.spm.portfolio.service;

import com.spm.portfolio.repository.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<UserDetails> findByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .map(user -> (UserDetails)user)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));

    }


    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUserName(username)
                .map(user -> (UserDetails)user)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));

    }
}

