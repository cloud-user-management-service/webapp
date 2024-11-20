package com.myweb.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.myweb.webapp.entity.EmailVerification;
import com.myweb.webapp.entity.User;
import com.myweb.webapp.repository.EmailVerificationRepository;
import com.myweb.webapp.repository.UserRepository;
import com.myweb.webapp.service.EmailVerificationService;

@Service
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

        User user = userRepo.findByEmail(email);
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

        //if the token is valid, update the status to 'verified' and return true
        user.setVerificationStatus(true);
        userRepo.save(user);

        return true;
    }
}
