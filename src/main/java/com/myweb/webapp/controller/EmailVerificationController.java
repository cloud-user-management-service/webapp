package com.myweb.webapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myweb.webapp.entity.EmailVerification;
import com.myweb.webapp.service.EmailVerificationService;
import com.myweb.webapp.service.impl.EmailVerificationServiceImpl;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/v1/user")
@Log4j2
public class EmailVerificationController {
    EmailVerificationService emailVerificationService;  

    public EmailVerificationController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }
    
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String token) {
        boolean isVerified = emailVerificationService.verifyToken(email, token);
        if (isVerified) {
            return ResponseEntity.ok("Email successfully verified.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification link is invalid or expired.");
        }
    }
}
