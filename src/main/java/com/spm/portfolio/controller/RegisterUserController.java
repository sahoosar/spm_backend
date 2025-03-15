package com.spm.portfolio.controller;

import com.spm.portfolio.model.User;
import com.spm.portfolio.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class RegisterUserController {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public Mono<User> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.createUser(user);
    }
    @GetMapping("/email/{email}")
    public Mono<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

}
