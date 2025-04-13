package com.example.ReceiptServer.service.oauth;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OAuthValidationService {
    private final TokenStorageService tokenStorageService;

    public boolean validateToken(String oAuthToken)
    {
        return false;
    }
}
