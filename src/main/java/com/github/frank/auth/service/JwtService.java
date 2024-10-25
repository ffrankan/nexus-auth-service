package com.github.frank.auth.service;

import com.github.frank.common.exception.BusinessException;
import com.github.frank.common.exception.ErrorCode;
import com.github.frank.system.entity.Role;
import com.github.frank.system.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author Frank An
 */
@Slf4j
@Service
public class JwtService {
    private final SecretKey key;
    private final long tokenValidityInMinutes;

    public record JwtTokenInfo(String username, Instant expiration) {
    }

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-minutes:30}") long tokenValidityInMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.tokenValidityInMinutes = tokenValidityInMinutes;
    }

    public String generateToken(User user) {
        var now = Instant.now();
        var expiration = now.plus(tokenValidityInMinutes, ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();
    }

    public JwtTokenInfo parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new JwtTokenInfo(
                    claims.getSubject(),
                    claims.getExpiration().toInstant());

        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired: {}", e.getMessage());
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            log.error("JWT token is invalid: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    public boolean validateToken(String token) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            // 检查是否过期
            if (claims.getPayload().getExpiration().toInstant().isBefore(Instant.now())) {
                log.warn("JWT token has expired");
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
