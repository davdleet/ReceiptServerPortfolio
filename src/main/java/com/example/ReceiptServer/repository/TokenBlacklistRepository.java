package com.example.ReceiptServer.repository;

import com.example.ReceiptServer.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, String> {
    @Modifying
    @Query("DELETE FROM TokenBlacklist t WHERE t.expirationDate < ?1")
    void deleteExpiredTokens(Instant now);


}
