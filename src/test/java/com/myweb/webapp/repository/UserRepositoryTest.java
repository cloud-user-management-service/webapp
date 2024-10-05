package com.myweb.webapp.repository;

import com.myweb.webapp.entity.User;
import com.myweb.webapp.repository.UserRepository;
import com.myweb.webapp.service.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("hashedPassword");
        user.setAccountCreated(LocalDateTime.now());
        user.setAccountUpdated(LocalDateTime.now());

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        User foundUser = userRepository.findByEmail("test@example.com");

        assertNotNull(foundUser, "User should not be null");
        assertEquals("test@example.com", foundUser.getEmail(), "Email should match");
        assertEquals("Test", foundUser.getFirstName(), "First name should match");
        assertEquals("User", foundUser.getLastName(), "Last name should match");

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }
}