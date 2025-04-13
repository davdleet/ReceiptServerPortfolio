package com.example.ReceiptServer.service.oauth;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

@Configuration
public class JwtConfig {
    @Bean(name = "kakaoJwtProcessor")
    public ConfigurableJWTProcessor<SecurityContext> kakaoJwtProcessor() throws Exception {
        return createJwtProcessor("https://kauth.kakao.com/.well-known/jwks.json");
    }

    @Bean(name = "googleJwtProcessor")
    public ConfigurableJWTProcessor<SecurityContext> googleJwtProcessor() throws Exception {
        return createJwtProcessor("https://www.googleapis.com/oauth2/v3/certs");
    }

    private ConfigurableJWTProcessor<SecurityContext> createJwtProcessor(String jwksUrl) throws Exception {
        RemoteJWKSet<SecurityContext> jwkSet = new RemoteJWKSet<>(new URL(jwksUrl));
        JWSVerificationKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSet);
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(keySelector);
        return jwtProcessor;
    }
}