package com.ideacollab.repository;

import com.ideacollab.model.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

// TokenBlacklistRepository.java
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByToken(String token);

    @Modifying
    @Query("DELETE FROM TokenBlacklist t WHERE t.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM TokenBlacklist t WHERE t.token = :token")
    void deleteByToken(@Param("token") String token);
}