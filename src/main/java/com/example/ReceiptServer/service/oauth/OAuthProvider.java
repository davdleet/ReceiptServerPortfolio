package com.example.ReceiptServer.service.oauth;


public interface OAuthProvider {
    OAuthUserInfo validateToken(String token) throws Exception;
    String getProviderName();
}
