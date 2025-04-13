package com.example.ReceiptServer.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 4, max = 20, message = "Username must be between 4-20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Username can only contain letters, numbers, '_', '-', or '.'")
    private String username;

    @Column(name="account_type", nullable = false)
    @NotBlank(message = "Account type cannot be empty")
    @Pattern(regexp = "^(naver|kakao|google|temp)$", message = "Invalid account type")
    private String accountType;

    @Column(name="notification", nullable = true, columnDefinition = "boolean default false")
    private boolean notification;

    @Column(name = "oauth_id")
    @JsonProperty("oAuthId")
    private String oAuthId;

    public User() {}

    public User(String username, String accountType) {
        this.username = username;
        this.accountType = accountType;
    }

    public User(String username, String accountType, String oAuthId) {
        this.username = username;
        this.accountType = accountType;
        this.oAuthId = oAuthId;
    }

    public boolean isOAuthAccount() {
        return "naver".equals(accountType) ||
                "kakao".equals(accountType) ||
                "google".equals(accountType);
    }

    @PrePersist
    @PreUpdate
    private void validateOAuthId() {
        if (isOAuthAccount() && (oAuthId == null || oAuthId.isEmpty())) {
            throw new IllegalStateException("OAuth users must have an oAuthId");
        }
    }


}
