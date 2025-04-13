package com.example.ReceiptServer.service.user;

import com.example.ReceiptServer.dto.AuthResponse;
import com.example.ReceiptServer.entity.User;
import com.example.ReceiptServer.repository.UserRepository;
import com.example.ReceiptServer.utils.AccessTokenUtil;
import com.example.ReceiptServer.utils.RefreshTokenUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccessTokenUtil accessTokenUtil;
    private final RefreshTokenUtil refreshTokenUtil;

    @Transactional
    public User createUser(User user)
    {

        if(usernameExists(user.getUsername()))
        {
            throw new IllegalArgumentException("Username already exists.");
        }

        return userRepository.save(user);
    }

    public AuthResponse createUserResponse(User user)
    {
        String accessToken = accessTokenUtil.generateTokenWithJti(user.getId(), user.getAccountType());
        String refreshToken = refreshTokenUtil.generateTokenWithJti(user.getId());
        AuthResponse authData = new AuthResponse(accessToken, refreshToken, user.getUsername(),
                user.getAccountType());
        return authData;
    }

    public User logInUserOAuth(String provider, String oAuthID, String oAuthToken)
    {
        return null;
    }

    public List<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    public Optional<User> getUserByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(UUID id){return userRepository.findUserById(id);};

    public boolean usernameExists(String username)
    {
        return userRepository.existsByUsername(username);
    }

    public User findOrCreateOAuthUser(String provider, String OAuthId, String username) {
        Optional<User> existingUser = userRepository.findByoAuthIdAndAccountType(OAuthId, provider);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        return userRepository.save(new User(username, provider, OAuthId));
    }
}
