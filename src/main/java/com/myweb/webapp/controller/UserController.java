package com.myweb.webapp.controller;

import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myweb.webapp.dto.CustomUserDetails;
import com.myweb.webapp.dto.UserRequestDto;
import com.myweb.webapp.dto.UserResponse;
import com.myweb.webapp.entity.User;
import com.myweb.webapp.service.MetricsService;
import com.myweb.webapp.service.UserService;
import com.myweb.webapp.service.impl.MetricsServiceImpl;
import com.myweb.webapp.exceptions.HandleBadRequestException;

import jakarta.servlet.http.HttpServletRequest;


import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/v1/user")
@Log4j2
public class UserController {
    UserService userService;
    MetricsService metricsService;
    
    @Value("${sns.topic.arn}")
    private String snsTopicArn;
    

    public UserController(UserService userService, MetricsService metricsService) {
        this.userService = userService;
        this.metricsService = metricsService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody Map<String, Object> userRequest) {
        long start = System.currentTimeMillis();

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

        // Publish SNS message
        try {
            log.info("snsTopicArn: {}", snsTopicArn);
            publishToSns(user);
            log.info("SNS message published successfully for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to publish SNS message for user: {}", user.getEmail(), e);
            // Handle failure (e.g., retry or alert)???????
            // throw new AmazonServiceException("Failed to publish SNS message for user: " + user.getEmail(), e);
        }

        long duration = System.currentTimeMillis() - start;
        metricsService.recordApiCall("create_user", duration);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
    //method to publish to SNS
    private void publishToSns(User user) {
        AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();

        Map<String, String> messagePayload = new HashMap<>();
        messagePayload.put("userId", String.valueOf(user.getId()));
        messagePayload.put("email", user.getEmail());
        messagePayload.put("firstName", user.getFirstName());
        messagePayload.put("lastName", user.getLastName());

        PublishRequest publishRequest;
        try {
            publishRequest = new PublishRequest()
                    .withTopicArn(snsTopicArn)
                    .withMessage(new ObjectMapper().writeValueAsString(messagePayload));
            PublishResult result = snsClient.publish(publishRequest);
            log.info("Message published to SNS with message ID: {}", result.getMessageId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message payload to JSON", e);
            // throw new AmazonServiceException("Failed to serialize message payload to JSON", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred: ", e);
            // throw new AmazonServiceException("Unexpected error occurred", e);
        }
    }

    

    @GetMapping("/self")
    public ResponseEntity<UserResponse> getUser(HttpServletRequest request) {
        long start = System.currentTimeMillis();

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

            long duration = System.currentTimeMillis() - start;
            metricsService.recordApiCall("get_user", duration);

            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/self")
    public ResponseEntity updateUser(@RequestBody Map<String, Object> userUpdateRequest, HttpServletRequest request) {
        long start = System.currentTimeMillis();

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

        long duration = System.currentTimeMillis() - start;
        metricsService.recordApiCall("update_user", duration);

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
