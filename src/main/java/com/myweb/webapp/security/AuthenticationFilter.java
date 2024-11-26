package com.myweb.webapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.myweb.webapp.exceptions.UserNotVerifiedException;

import java.util.Base64;

import java.io.IOException;

@Component
@Log4j2
public class AuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationFilter(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Skip authentication for health check and user registration
        String requestURI = request.getRequestURI();
        
        if (requestURI.equals("/v1/user") || requestURI.equals("/healthz") || requestURI.equals("/cicd") || requestURI.equals("/v1/user/verify")) {
            filterChain.doFilter(request, response); 
            return; 
        }

        String requestHeader = request.getHeader("Authorization");
        log.info("Header: {}", requestHeader);

        log.info("requestURI"+requestURI);
        log.info("equestHeader"+requestHeader);

        if (requestHeader != null && requestHeader.startsWith("Basic ")) {
            try {
                String authenticationString = requestHeader.substring(6);

                log.info("thenticationStrin"+authenticationString);

                String[] credentials = extractUserNameAndPassword(authenticationString);

                log.info(credentials[0]);

                String username = credentials[0];
                String rawPassword = credentials[1];

                // Load user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validate password
                if (userDetails != null && passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
                    // Optionally set the authentication context here if you want to use it in the security context
                    request.setAttribute("userDetails", userDetails);
                    log.info("User authenticated successfully.");
                } else {
                    log.warn("Authentication failed: invalid username or password");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } catch (UsernameNotFoundException e) {
                log.error("Error processing authentication, username not found", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (UserNotVerifiedException e) {
                log.error("Error processing authentication, unverified user", e);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            } catch (Exception e) {
                log.error("Error processing authentication", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            log.warn("Authorization header is missing or invalid.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    private String[] extractUserNameAndPassword(String authenticationString) {
        String decodedString = new String(Base64.getDecoder().decode(authenticationString));
        return decodedString.split(":");
    }
}

