package com.myweb.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.myweb.webapp.entity.EmailVerification;
import com.myweb.webapp.entity.User;
import com.myweb.webapp.repository.EmailVerificationRepository;
import com.myweb.webapp.repository.UserRepository;
import com.myweb.webapp.service.EmailVerificationService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private EmailVerificationRepository emailVerificationRepo;
    private EmailVerification emailVerification;
    private UserRepository userRepo;

    public EmailVerificationServiceImpl(EmailVerificationRepository emailVerificationRepository) {   
        this.emailVerificationRepo = emailVerificationRepository;
    }

    public boolean verifyToken(String email, String token) {
        // search for the token in the database
        emailVerification = emailVerificationRepo.findByToken(token)
                .orElse(null);
        log.info("EmailVerification: " + emailVerification);
        User user = userRepo.findByEmail(email);
        log.info("User: " + user);
        if (emailVerification == null || user.isVerificationStatus()) {
            return false; // if the token is not found or the user is already verified, return false
        }

        // Check if the token has expired
        LocalDateTime expiresAt = emailVerification.getExpireTime();
        if (expiresAt.isBefore(LocalDateTime.now())) {
            user.setVerificationStatus(false);
            userRepo.save(user);
            return false;
        }
        log.info("Token has not expired: " + expiresAt);
        //if the token is valid, update the status to 'verified' and return true
        user.setVerificationStatus(true);
        userRepo.save(user);

        log.info("User email has been verified: " + email);
        return true;
    }
}
