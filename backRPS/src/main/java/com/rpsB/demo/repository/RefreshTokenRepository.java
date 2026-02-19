package com.rpsB.demo.repository;

import com.rpsB.demo.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    @Query("""
            SELECT r FROM RefreshToken r 
            WHERE r.id=:id            
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findByRefreshId(String id);

    @Query("""
            SELECT r 
            FROM RefreshToken r
            WHERE r.user.id = :userId
            AND r.revoked = false
            ORDER BY r.createdAt ASC
            """)
    List<RefreshToken> findByUserIdAndRevokedFalseOrderByCreatedAtAsc(Long userId);
}
