package com.rpsB.demo.security.jwt;

import com.rpsB.demo.entity.User;
import com.rpsB.demo.repository.UserRepository;
import com.rpsB.demo.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ApiException;
import org.hibernate.tool.schema.spi.SchemaTruncator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret.key}")
    private String secretKey;
    @Value("${access.token.expired}")
    private Long accessTokenExpire;
    @Value("${refresh.token.expired}")
    private Long refreshTokenExpire;
    private SecretKey key;

    private final UserRepository userRepository;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Authentication authentication) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenExpire * 60 * 60 * 1000);
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();

        return Jwts.builder()
                .subject(user.getId().toString())
                .id(UUID.randomUUID().toString())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenExpire * 60 * 60 * 1000);
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();

        return Jwts.builder()
                .subject(user.getId().toString())
                .id(UUID.randomUUID().toString())
                .signWith(key)
                .issuedAt(now)
                .expiration(validity)
                .compact();

    }

    public boolean validateToken(String accessToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(accessToken);

            return !claimsJws
                    .getPayload()
                    .getExpiration()
                    .before(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }

    }

    public Authentication getAuthenticationExpiredToken(String accessToken) {
        String subject;
        try {
            subject = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            subject = e.getClaims().getSubject();
        }
        Long userId = Long.parseLong(subject);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        UserPrincipal userPrincipal = new UserPrincipal(user);
        return new UsernamePasswordAuthenticationToken(userPrincipal,
                null, userPrincipal.getAuthorities());
    }

    public Authentication getAuthentication(String accessToken) {
        String subject = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .getSubject();
        Long userId = Long.parseLong(subject);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        UserPrincipal userPrincipal = new UserPrincipal(user);
        return new UsernamePasswordAuthenticationToken(userPrincipal,
                null, userPrincipal.getAuthorities());

    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
