package com.rpsB.demo.service;

import com.rpsB.demo.entity.RefreshToken;
import com.rpsB.demo.entity.User;
import com.rpsB.demo.exception.AppException;
import com.rpsB.demo.repository.RefreshTokenRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Refresh Token Service Unit Test")
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private EntityManager entityManager;
    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void save_shouldPersistToken() {

        Long userId = 1L;
        String jti = "token-id";
        Date expiration = new Date(System.currentTimeMillis() + 10_000);

        User userRef = User.builder().build();
        when(entityManager.getReference(User.class, userId))
                .thenReturn(userRef);

        refreshTokenService.save(jti, expiration, userId);

        verify(refreshTokenRepository).save(argThat(token ->
                token.getId().equals(jti) &&
                        token.getUser().equals(userRef) &&
                        !token.isRevoked()
        ));
    }

    @Test
    void revoke_shouldSetRevokedTrue() {

        RefreshToken token = new RefreshToken();
        token.setRevoked(false);

        refreshTokenService.revoke(token);

        assertTrue(token.isRevoked());
    }

    @Test
    void validateAndGet_shouldThrowIfRevoked() {

        RefreshToken token = new RefreshToken();
        token.setRevoked(true);
        token.setExpiresAt(Instant.now().plusSeconds(60));

        when(refreshTokenRepository.findByRefreshId("jti"))
                .thenReturn(Optional.of(token));

        assertThrows(AppException.class,
                () -> refreshTokenService.validateAndGet("jti"));
    }

    @Test
    void validateAndGet_shouldThrowIfNotFound() {

        when(refreshTokenRepository.findByRefreshId("jti"))
                .thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> refreshTokenService.validateAndGet("jti"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void validateAndGet_shouldThrowIfExpired() {

        RefreshToken token = new RefreshToken();
        token.setRevoked(false);
        token.setExpiresAt(Instant.now().minusSeconds(60));

        when(refreshTokenRepository.findByRefreshId("jti"))
                .thenReturn(Optional.of(token));

        AppException ex = assertThrows(AppException.class,
                () -> refreshTokenService.validateAndGet("jti"));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void validateAndGet_shouldReturnTokenIfValid() {

        RefreshToken token = new RefreshToken();
        token.setRevoked(false);
        token.setExpiresAt(Instant.now().plusSeconds(60));

        when(refreshTokenRepository.findByRefreshId("jti"))
                .thenReturn(Optional.of(token));

        RefreshToken result =
                refreshTokenService.validateAndGet("jti");

        assertEquals(token, result);
    }

    @Test
    void enforceLimit_shouldDoNothingIfBelowLimit() {

        List<RefreshToken> tokens = List.of(
                new RefreshToken(),
                new RefreshToken()
        );

        when(refreshTokenRepository
                .findByUserIdAndRevokedFalseOrderByCreatedAtAsc(1L))
                .thenReturn(tokens);

        refreshTokenService.enforceLimit(1L, 5);

        tokens.forEach(t -> assertFalse(t.isRevoked()));
    }

    @Test
    void enforceLimit_shouldRevokeOldestTokens() {

        RefreshToken t1 = new RefreshToken();
        RefreshToken t2 = new RefreshToken();
        RefreshToken t3 = new RefreshToken();

        t1.setRevoked(false);
        t2.setRevoked(false);
        t3.setRevoked(false);

        List<RefreshToken> tokens =
                new ArrayList<>(List.of(t1, t2, t3));

        when(refreshTokenRepository
                .findByUserIdAndRevokedFalseOrderByCreatedAtAsc(1L))
                .thenReturn(tokens);

        refreshTokenService.enforceLimit(1L, 2);

        assertTrue(t1.isRevoked());
        assertTrue(t2.isRevoked());
        assertFalse(t3.isRevoked());
    }
}