package com.myweb.webapp.service;

public interface EmailVerificationService {
    boolean verifyToken(String email, String token);

}
