// File: src/test/java/com/example/ReceiptServer/controller/AuthServiceTestConfig.java
package com.example.ReceiptServer.controller;

import com.example.ReceiptServer.service.oauth.AuthService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AuthServiceTestConfig {

    @Bean
    public AuthService authService() {
        return Mockito.mock(AuthService.class);
    }
}