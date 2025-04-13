package com.example.ReceiptServer.repository;

import com.example.ReceiptServer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByoAuthIdAndAccountType(String oAuthId, String accountType);
    Optional<User> findUserById(UUID id);
    
    boolean existsByUsername(String username);
}
