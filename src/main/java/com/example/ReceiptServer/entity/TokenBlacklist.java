// src/main/java/com/example/ReceiptServer/entity/TokenBlacklist.java
package com.example.ReceiptServer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "token_blacklist")
public class TokenBlacklist {

    @Id
    private String tokenId;

    private Instant expirationDate;

    public TokenBlacklist() {}

    public TokenBlacklist(String tokenId, Instant expirationDate) {
        this.tokenId = tokenId;
        this.expirationDate = expirationDate;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
    }
}