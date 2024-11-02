package com.myweb.webapp.service.impl;

import java.time.LocalDateTime;

import java.util.Map;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myweb.webapp.dto.UserRequestDto;
import com.myweb.webapp.entity.User;
import com.myweb.webapp.exceptions.DatabaseAccessException;
import com.myweb.webapp.exceptions.EmailExistsException;
import com.myweb.webapp.repository.UserRepository;
import com.myweb.webapp.service.UserService;

import com.myweb.webapp.exceptions.ParamException;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;
    private MetricsServiceImpl metricsService;

    // @Autowired
    public UserServiceImpl(UserRepository userRepo, PasswordEncoder passwordEncoder, MetricsServiceImpl metricsService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.metricsService = metricsService;
    }

    @Override
    public User createUser(Map<String, Object> userRequest) {
        validateUserRequestDto(userRequest);

        try {
            // Check if user with email already exists
            User existingUser = userRepo.findByEmail((String) userRequest.get("email"));

            if (existingUser != null) {
                // If the email already exists, throw a custom exception
                throw new EmailExistsException();
            }
        } catch (DataAccessException e) {
            log.error("Database error when searching for existing email: {}", e.getMessage());
            throw new DatabaseAccessException();
        }

        User user = new User();
        user.setEmail((String) userRequest.get("email"));
        user.setFirstName((String) userRequest.get("first_name"));
        user.setLastName((String) userRequest.get("last_name"));
        user.setPassword(passwordEncoder.encode((String) userRequest.get("password")));
        user.setAccountCreated(LocalDateTime.now());
        user.setAccountUpdated(LocalDateTime.now());

        long startDB = System.currentTimeMillis();
        try {
            User savedUser = userRepo.save(user); // Save the user
            long durationDB = System.currentTimeMillis() - startDB; // Calculate duration
            metricsService.recordDatabaseQuery("create_user", durationDB); // Record duration
            return savedUser; // Return the saved user
        } catch (DataAccessException e) {
            log.error("Database error when saving user: {}", e.getMessage());
            throw new DatabaseAccessException(); // Throw custom exception
        }
    }

    public ResponseEntity updateUser(Map<String, Object> userUpdateRequest, User currentUser) {
        // Update allowed fields only and set the updated flag to false
        boolean updated = false;

        if (userUpdateRequest.containsKey("first_name")) {
            Object firstName = userUpdateRequest.get("first_name");
            if (firstName instanceof String) {
                currentUser.setFirstName((String) firstName);
                updated = true;
            }
        }

        if (userUpdateRequest.containsKey("last_name")) {
            Object lastName = userUpdateRequest.get("last_name");
            if (lastName instanceof String) {
                currentUser.setLastName((String) lastName);
                updated = true;
            }
        }

        if (userUpdateRequest.containsKey("password")) {
            Object password = userUpdateRequest.get("password");
            if (password instanceof String) {
                currentUser.setPassword(passwordEncoder.encode((String) password));
                updated = true;
            }
        }

        // If any allowed field was updated, update account_updated and save the user
        if (updated) {
            currentUser.setAccountUpdated(LocalDateTime.now()); // Update the account timestamp

            long startDB = System.currentTimeMillis(); 
            try {
                userRepo.save(currentUser); 
                long durationDB = System.currentTimeMillis() - startDB; 
                metricsService.recordDatabaseQuery("update_user", durationDB); 

                log.info("User saved in {} ms", durationDB); 
                // record the duration
            } catch (DataAccessException e) {
                log.error("Database error when saving user: {}", e.getMessage());
                throw new DatabaseAccessException(); // Handle exception appropriately
            }

            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No content on success
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    private void validateUserRequestDto(Map<String, Object> userRequest) {
        if (userRequest == null || !userRequest.containsKey("email") || !userRequest.containsKey("first_name")
                || !userRequest.containsKey("last_name") || !userRequest.containsKey("password")) {
            throw new ParamException();
        }

        // Check for unexpected fields
        Set<String> allowedFields = new HashSet<>(Arrays.asList("email", "first_name", "last_name", "password"));
        for (String key : userRequest.keySet()) {
            if (!allowedFields.contains(key)) {
                throw new ParamException();
            }
        }

        // Validate email format
        String email = (String) userRequest.get("email");
        if (email == null || !email.contains("@")) {
            throw new ParamException();
        }

        // Validate password
        String password = (String) userRequest.get("password");
        if (password == null || password.isEmpty()) {
            throw new ParamException();
        }
    }

}