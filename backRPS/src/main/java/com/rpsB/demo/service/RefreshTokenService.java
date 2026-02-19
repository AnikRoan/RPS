package com.rpsB.demo.service;

import com.rpsB.demo.entity.RefreshToken;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.repository.RefreshTokenRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final EntityManager entityManager;


    public void save(String jti, Date expiration, Long userPrincipalId) {
        RefreshToken token = new RefreshToken();
        token.setId(jti);
        token.setUser(entityManager.getReference(User.class, userPrincipalId));
        token.setExpiresAt(expiration.toInstant());
        token.setCreatedAt(Instant.now());
        token.setRevoked(false);

        refreshTokenRepository.save(token);
    }

    public void revoke(RefreshToken token) {
        token.setRevoked(true);
    }

    public RefreshToken validateAndGet(String jti) {
        RefreshToken token = refreshTokenRepository.findByRefreshId(jti)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (token.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return token;
    }


    public void enforceLimit(Long userId, int maxTokens) {
        List<RefreshToken> tokens =
                refreshTokenRepository
                        .findByUserIdAndRevokedFalseOrderByCreatedAtAsc(userId);
        if (tokens.size() < maxTokens) {
            return;
        }
        int toDelete = tokens.size() - maxTokens + 1;

        for (int i = 0; i < toDelete; i++) {
            tokens.get(i).setRevoked(true);
        }
    }
}
