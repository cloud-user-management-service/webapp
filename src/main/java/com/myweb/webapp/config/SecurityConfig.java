package com.myweb.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.myweb.webapp.security.AuthenticationFilter;
import com.myweb.webapp.service.impl.MyUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationFilter authenticationFilter;
    private MyUserDetailsService myUserDetailsService;  
    private RestAuthenticationEntryPoint authenticationEntryPoint;
    private PasswordEncoderConfig passwordEncoderConfig;

    
    public SecurityConfig(MyUserDetailsService myUserDetailsService, AuthenticationFilter authenticationFilter, RestAuthenticationEntryPoint authenticationEntryPoint, PasswordEncoderConfig passwordEncoderConfig) {
        this.myUserDetailsService = myUserDetailsService;
        this.authenticationFilter = authenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }
    

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF protection for stateless REST APIs
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/healthz").permitAll() // Allow access to /healthz without authentication
                .requestMatchers("/v1/user").permitAll() // Allow access to /v1/user without authentication
                .requestMatchers("/v1/user/verify").permitAll() // Allow access to /v1/user/verify without authentication
                .requestMatchers("/cicd").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll() 
                .requestMatchers("/v3/api-docs/**").permitAll() 
                .anyRequest().authenticated()             // All other requests require authentication
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session management
            .httpBasic(httpBasic -> 
                httpBasic.authenticationEntryPoint(authenticationEntryPoint)
            );
            http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(myUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(PasswordEncoderConfig.passwordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    


}
