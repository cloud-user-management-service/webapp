package com.myweb.webapp.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.myweb.webapp.dto.CustomUserDetails;
import com.myweb.webapp.entity.User;
import com.myweb.webapp.repository.UserRepository;


@Service
public class MyUserDetailsService implements UserDetailsService {    
    private UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username does not exist");
        }
        String password = user.getPassword();
        return new CustomUserDetails(user, password);

    }

    
}
