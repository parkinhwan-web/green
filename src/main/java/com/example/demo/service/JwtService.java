package com.example.demo.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    // ✅ JwtUtil과 동일한 고정 시크릿 키 (Base64 인코딩 불필요)
    private static final String SECRET = "my-super-secure-and-long-secret-key-1234567890";
    private static final SecretKey SIGNING_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    private SecretKey getSigningKey() {
        return SIGNING_KEY;
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000)) // 1시간 유효
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        if (blacklistedTokens.contains(token)) {
            logger.warn("\uD83D\uDEAB 블랙리스트된 토큰입니다: {}", token);
            return false;
        }
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token.trim());
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("\u274C JWT 만료됨: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("\u274C 지원되지 않는 JWT 형식: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("\u274C JWT 형식이 올바르지 않음: {}", e.getMessage());
        } catch (SecurityException e) {
            logger.error("\u274C JWT 서명 검증 실패: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("\u274C JWT가 비어 있음: {}", e.getMessage());
        }
        return false;
    }

    public String extractEmail(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token.trim())
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            logger.error("\u274C JWT에서 이메일 추출 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token.trim())
                    .getBody()
                    .get("roles", String.class);
        } catch (Exception e) {
            logger.error("\u274C JWT에서 역할 추출 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    public void invalidateToken(String token) {
        blacklistedTokens.add(token);
        logger.info("\uD83D\uDEAB 토큰 블랙리스트 추가됨: {}", token);
    }
}

