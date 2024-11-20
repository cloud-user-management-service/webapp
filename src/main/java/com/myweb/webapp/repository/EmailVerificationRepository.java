package com.myweb.webapp.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myweb.webapp.entity.EmailVerification;
import com.myweb.webapp.entity.ImageMetadata;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {
    Optional<EmailVerification> findByToken(String token);
}
