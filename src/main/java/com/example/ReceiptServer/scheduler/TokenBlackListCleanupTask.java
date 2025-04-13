package com.example.ReceiptServer.scheduler;

import com.example.ReceiptServer.repository.TokenBlacklistRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenBlackListCleanupTask {
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public TokenBlackListCleanupTask(TokenBlacklistRepository tokenBlacklistRepository)
    {
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredTokens()
    {
        tokenBlacklistRepository.deleteExpiredTokens(Instant.now());
    }
}
