package com.example.ReceiptServer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "User authentication response")
public class AuthResponse {
    @Schema(description = "JWT access token for API authorization", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "JWT refresh token to obtain new access tokens", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "User's username", example = "john_doe")
    private String username;

    @Schema(description = "Type of account", example = "temp", allowableValues = {"temp", "kakao", "google"})
    private String accountType;
}
