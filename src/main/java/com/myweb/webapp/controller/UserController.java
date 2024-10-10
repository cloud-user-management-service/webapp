package com.myweb.webapp.controller;

import java.util.Map;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myweb.webapp.dto.CustomUserDetails;
import com.myweb.webapp.dto.UserRequestDto;
import com.myweb.webapp.dto.UserResponse;
import com.myweb.webapp.entity.User;
import com.myweb.webapp.service.UserService;
import com.myweb.webapp.exceptions.HandleBadRequestException;

import jakarta.servlet.http.HttpServletRequest;


import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/v1/user")
@Log4j2
public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody Map<String, Object> userRequest) {
        User user = userService.createUser(userRequest);

        // map user to userResponse
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setAccountCreated(user.getAccountCreated());
        userResponse.setAccountUpdated(user.getAccountUpdated());

        
        log.info("User created with email: {}", user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @GetMapping("/self")
    public ResponseEntity<UserResponse> getUser(HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // if user is an instance of CustomUserDetails, return user details
        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            User user = customUserDetails.getUser();

            UserResponse userResponse = new UserResponse(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getAccountCreated(),
                    user.getAccountUpdated());

            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/self")
    public ResponseEntity updateUser(@RequestBody Map<String, Object> userUpdateRequest, HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Allowed fields
        String[] allowedFields = { "first_name", "last_name", "password" };

        // Check for disallowed fields in the incoming request
        for (String key : userUpdateRequest.keySet()) {
            if (!isFieldAllowed(key, allowedFields)) {
                throw new HandleBadRequestException();
            }
        }

        // if user is an instance of CustomUserDetails, return user details
        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            User currentUser = customUserDetails.getUser();
            try {

                userService.updateUser(userUpdateRequest, currentUser);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    private boolean isFieldAllowed(String fieldName, String[] allowedFields) {
        for (String allowedField : allowedFields) {
            if (allowedField.equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

}
