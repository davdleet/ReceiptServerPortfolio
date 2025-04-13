package com.example.ReceiptServer.service.oauth;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OAuthProviderFactory {
    private final Map<String, OAuthProvider> providers;

    public OAuthProviderFactory(List<OAuthProvider> providerList)
    {
        providers = providerList.stream()
                .collect(Collectors.toMap(
                        OAuthProvider::getProviderName,
                        provider -> provider
                ));
    }

    public OAuthProvider getProvider(String providerName)
    {
        OAuthProvider provider = providers.get(providerName.toLowerCase());
        if (provider == null)
        {
            throw new IllegalArgumentException("Unsupported OAuth provider: " + providerName);
        }
        return provider;
    }
}
