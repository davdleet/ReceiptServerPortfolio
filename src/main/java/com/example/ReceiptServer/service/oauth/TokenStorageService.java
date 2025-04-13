package com.example.ReceiptServer.service.oauth;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenStorageService {
    private final Map<String, UUID> activeTokens = new ConcurrentHashMap<>();
    public void storeToken(String tokenId, UUID userId) {
        activeTokens.put(tokenId, userId);
    }

    public boolean isTokenValid(String tokenId)
    {
        return activeTokens.containsKey(tokenId);
    }

    public void revokeToken(String tokenId)
    {
        activeTokens.remove(tokenId);
    }
}
