package com.spm.portfolio.service.user;
import com.spm.portfolio.model.User;
import com.spm.portfolio.repository.UserRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Random;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final DatabaseClient databaseClient;

    public UserService(UserRepository userRepository, DatabaseClient databaseClient) {
        this.userRepository = userRepository;
        this.databaseClient = databaseClient;
    }

    public Mono<User> createUser(User user) {

        if (user.getUserId() == null) {
            Random random = new Random();
            int sixDigitNumber = 100000 + random.nextInt(900000); // Generates a num
            user.setUserId("SPM_"+sixDigitNumber);  // Ensure UUID is set before saving
        }
        String sql = "INSERT INTO users (user_id, user_name, user_email, phone_number, password, active) " +
                "VALUES (:userId, :userName, :userEmail, :phoneNumber, :password, :active)";

        return databaseClient.sql(sql)
                .bind("userId", user.getUserId())
                .bind("userName", user.getUsername())
                .bind("userEmail", user.getUserEmail())
                .bind("phoneNumber", user.getPhoneNumber())
                .bind("password", user.getPassword())
                .bind("active", user.isActive())
                .fetch()
                .rowsUpdated()
                .flatMap(rows -> {
                    if (rows > 0) {
                        return Mono.just(user);
                    } else {
                        return Mono.error(new RuntimeException("Failed to insert user"));
                    }
                })
                .doOnSuccess(savedUser -> System.out.println("User inserted successfully: " + savedUser))
                .doOnError(error -> System.err.println("Error inserting user: " + error.getMessage()));

    }

    public Mono<User> getUserByEmail(String email) {
        return userRepository.findByUserEmail(email);
    }
}

