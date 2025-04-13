package com.example.ReceiptServer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("!test")
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("🔒 Security Configuration Loaded!");

        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF for APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/**").permitAll()// ✅ Allow all requests to /users/.requestMatchers("/status").permitAll()  // ✅ Allow all requests to /status/*
                        .requestMatchers("/api/status").permitAll()
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}