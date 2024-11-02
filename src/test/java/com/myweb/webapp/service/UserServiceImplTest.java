package com.myweb.webapp.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.myweb.webapp.config.PasswordEncoderConfig;
import com.myweb.webapp.dto.UserRequestDto;
import com.myweb.webapp.entity.User;
import com.myweb.webapp.exceptions.EmailExistsException;
import com.myweb.webapp.repository.UserRepository;
import com.myweb.webapp.service.impl.MetricsServiceImpl;
import com.myweb.webapp.service.impl.UserServiceImpl;

import software.amazon.awssdk.http.nio.netty.internal.http2.MultiplexedChannelRecord.Metrics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private MetricsService metricsService; 

    // @MockBean
    // private PasswordEncoderConfig passwordEncoderConfig;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        // No need to initialize mocks manually since @MockBean is used
    }

    // @Test
    // void testCreateUser_Success() {
    //     // Prepare a user object
    //     User user = new User();
    //     user.setEmail("test@example.com");
    //     user.setFirstName("Test");
    //     user.setLastName("User");
    //     user.setPassword("hashedPassword");

    //     // Mock the behavior of passwordEncoder to return a hashed password
    //     when(passwordEncoder.encode(any())).thenReturn(user.getPassword());
    //     // when(passwordEncoderConfig.passwordEncoder()).thenReturn(new
    //     // BCryptPasswordEncoder());

    //     // Mock the repository save method
    //     when(userRepository.save(any(User.class))).thenReturn(user);

    //     // Call the method being tested
    //     Map<String, Object> userRequest = new HashMap<>();
    //     userRequest.put("email", "test@example.com");
    //     userRequest.put("first_name", "Test");
    //     userRequest.put("last_name", "User");
    //     userRequest.put("password", "password123");
    //     User result = userService.createUser(userRequest);

    //     assertNotNull(result);
    //     assertEquals("test@example.com", result.getEmail());
    //     verify(userRepository, times(1)).save(any(User.class));
    // }

    // @Test
    // void testCreateUser_EmailExists() {
    //     when(userRepository.findByEmail("existing@example.com")).thenReturn(new User());

    //     Map<String, Object> userRequest = new HashMap<>();
    //     userRequest.put("email", "existing@example.com");
    //     userRequest.put("first_name", "Existing");
    //     userRequest.put("last_name", "User");
    //     userRequest.put("password", "password123");

    //     assertThrows(EmailExistsException.class, () -> {
    //         userService.createUser(userRequest);
    //     });

    //     verify(userRepository, never()).save(any(User.class));
    // }
}
