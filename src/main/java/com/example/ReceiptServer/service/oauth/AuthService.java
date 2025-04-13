package com.example.ReceiptServer.service.oauth;

import com.example.ReceiptServer.dto.AuthResponse;
import com.example.ReceiptServer.dto.OAuthRequest;
import com.example.ReceiptServer.entity.User;
import com.example.ReceiptServer.exception.InvalidOAuthTokenException;
import com.example.ReceiptServer.exception.UserNotFoundException;
import com.example.ReceiptServer.repository.TokenBlacklistRepository;
import com.example.ReceiptServer.service.user.UserService;
import com.example.ReceiptServer.utils.AccessTokenUtil;
import com.example.ReceiptServer.utils.RefreshTokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserService userService;
    private final AccessTokenUtil accessTokenUtil;
    private final RefreshTokenUtil refreshTokenUtil;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final KakaoOAuthProvider kakaoOAuthProvider;


    public AuthResponse reissueTokens(String refreshToken) {
        if (!refreshTokenUtil.validateToken(refreshToken)) {
            throw new InvalidOAuthTokenException("Invalid or expired refresh token");
        }

        // Check if token has less than one week remaining (604,800,000 milliseconds)
        long remainingTime = refreshTokenUtil.getTokenTimeRemaining(refreshToken);
        //final long ONE_WEEK_MILLIS = 604_800_000L;
        final long ONE_WEEK_MILLIS = 604_800_000_000_000L;
        if (remainingTime > ONE_WEEK_MILLIS) {
            throw new InvalidOAuthTokenException("Token reissuance allowed only if less than one week of validity remains");
        }

        UUID userId = refreshTokenUtil.getUserIdFromToken(refreshToken);
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        // Revoke the old token
        refreshTokenUtil.revokeToken(refreshToken);

        // Generate new tokens
        String newAccessToken = accessTokenUtil.generateTokenWithJti(userId, user.getAccountType());
        String newRefreshToken = refreshTokenUtil.generateTokenWithJti(userId);

        return new AuthResponse(newAccessToken, newRefreshToken, user.getUsername(), user.getAccountType());
    }

    public String reissueAccessToken(String refreshToken) {
        if (!refreshTokenUtil.validateToken(refreshToken)) {
            throw new InvalidOAuthTokenException("Invalid or expired refresh token");
        }
        UUID userId = refreshTokenUtil.getUserIdFromToken(refreshToken);
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException());
        return accessTokenUtil.generateTokenWithJti(userId, user.getAccountType());
    }

    public long getTokenTimeRemaining(String token) {
        return refreshTokenUtil.getTokenTimeRemaining(token);
    }

    @Transactional
    public AuthResponse oauthLogin(String provider, OAuthRequest request) {
        // oAuthId holds id returned from the oAuth Provider
        String oAuthId = "";
        if (provider.equals("kakao"))
        {
            oAuthId = kakaoOAuthProvider.validateToken(request.getToken()).getId();
        }
        User user = userService.findOrCreateOAuthUser(provider, oAuthId, request.getUsername());
        if (user == null) {
            throw new InvalidOAuthTokenException("Invalid OAuth token provided");
        }
        String accessToken = accessTokenUtil.generateTokenWithJti(user.getId(), user.getAccountType());
        String refreshToken = refreshTokenUtil.generateTokenWithJti(user.getId());
        return new AuthResponse(accessToken, refreshToken, user.getUsername(), user.getAccountType());
    }

    public boolean validateRefreshToken(String token) {
        return refreshTokenUtil.validateToken(token);
    }

}