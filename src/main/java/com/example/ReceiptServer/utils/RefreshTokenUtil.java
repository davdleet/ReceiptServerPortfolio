// RefreshTokenUtil.java
package com.example.ReceiptServer.utils;

import com.example.ReceiptServer.entity.TokenBlacklist;
import com.example.ReceiptServer.repository.TokenBlacklistRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class RefreshTokenUtil {
    private final TokenBlacklistRepository tokenBlacklistRepository;
    @Value("${JWT_SECRET_KEY}")
    private String secretKey;
    private static final long REFRESH_TOKEN_EXPIRATION = 2_592_000_000L; // 30 days

    private Key key;

    public RefreshTokenUtil(TokenBlacklistRepository tokenBlacklistRepository) {
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    @PostConstruct
    private void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    public boolean validateToken(String token) {
        try {
            String tokenId = getTokenIdFromToken(token);

            if (tokenBlacklistRepository.existsById(tokenId))
            {
                return false;
            }

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extract user ID from token
    public UUID getUserIdFromToken(String token) {
        String id = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return UUID.fromString(id);
    }

    // Generate token with a unique token ID for revocation
    public String generateTokenWithJti(UUID userId) {
        String tokenId = UUID.randomUUID().toString();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setId(tokenId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    // Get token ID from refresh token
    public String getTokenIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getId();
    }

    public Date getExpirationDateFromToken(String token)
    {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public long getTokenTimeRemaining(String token)
    {
        try {
            Date expiration = getExpirationDateFromToken(token);
            long now = System.currentTimeMillis();
            long expirationTime = expiration.getTime();

            if (expirationTime <= now)
            {
                return 0;
            }

            return expirationTime - now;
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public void revokeToken(String token)
    {
        try {
            Date expiration = getExpirationDateFromToken(token);
            String tokenId = getTokenIdFromToken(token);

            TokenBlacklist blacklistedToken = new TokenBlacklist(
                    tokenId,
                    expiration.toInstant()
            );
            tokenBlacklistRepository.save(blacklistedToken);
        }
        catch (Exception e)
        {
            System.out.println("Error while revoking token");
        }
    }
}