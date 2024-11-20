package com.myweb.webapp.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.myweb.webapp.dto.CustomUserDetails;
import com.myweb.webapp.entity.User;
import com.myweb.webapp.repository.UserRepository;
import com.myweb.webapp.service.MetricsService;

import org.springframework.security.authentication.DisabledException;


@Service
public class MyUserDetailsService implements UserDetailsService {    
    private UserRepository userRepository;
    private MetricsService metricsService;

    public MyUserDetailsService(UserRepository userRepository, MetricsService metricsService) {
        this.userRepository = userRepository;
        this.metricsService = metricsService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        long start = System.currentTimeMillis();
        User user = userRepository.findByEmail(username);
        long duration = System.currentTimeMillis() - start;
        metricsService.recordDatabaseQuery("find_user_by_email", duration);
        
        if (user == null) {
            throw new UsernameNotFoundException("Username does not exist");
        }

        if (!user.isVerificationStatus()) {
            throw new DisabledException("User email is not verified"); 
        }

        String password = user.getPassword();
        return new CustomUserDetails(user, password);

    }

    
}
