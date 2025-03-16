package com.spm.portfolio.service;

import com.spm.portfolio.model.User;
import com.spm.portfolio.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.FetchSpec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DatabaseClient databaseClient;

    @Mock
    private DatabaseClient.GenericExecuteSpec executeSpec;


    private FetchSpec<Long> fetchSpec; // Mock FetchSpec for rowsUpdated()

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fetchSpec = mock(FetchSpec.class);

        when(fetchSpec.rowsUpdated()).thenReturn(Mono.just(1l));
        // Mock DatabaseClient SQL Execution
        when(databaseClient.sql(anyString())).thenReturn(executeSpec);
        when(executeSpec.bind(anyString(), any())).thenReturn(executeSpec);
    }

    @Test
    void testGetUserByEmail_Success() {
        // Mock user data
        User mockUser = new User();
        mockUser.setUserId("SPM_123456");
        mockUser.setUserName("John Doe");
        mockUser.setUserEmail("saroj@example.com");
        mockUser.setPhoneNumber("1234567890");
        mockUser.setPassword("securePassword");
        mockUser.setActive(true);

        // Mock repository behavior
        when(userRepository.findByUserEmail("saroj@example.com")).thenReturn(Mono.just(mockUser));

        // Execute test
        StepVerifier.create(userService.getUserByEmail("saroj@example.com"))
                .assertNext(user -> {
                    assertEquals("SPM_123456", user.getUserId());
                    assertEquals("John Doe", user.getUsername());
                })
                .verifyComplete();

        // Verify repository interaction
        verify(userRepository).findByUserEmail("saroj@example.com");
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userRepository.findByUserEmail("unknown@example.com")).thenReturn(Mono.empty());

        // Execute test
        StepVerifier.create(userService.getUserByEmail("unknown@example.com"))
                .expectNextCount(0)  // Expect no results
                .verifyComplete();

        // Verify repository interaction
        verify(userRepository).findByUserEmail("unknown@example.com");
    }
}
