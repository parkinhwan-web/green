package com.example.demo.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    // ✅ 반드시 Base64로 인코딩된 시크릿 키여야 함 (최소 32바이트 인코딩 기준)
    @Value("${jwt.secret:ZGVmYXVsdEJhc2U2NFNlY3JldEtleTEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNA==}") 
    private String secretKey;

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    private Key getSigningKey() {
        String trimmedKey = secretKey.trim();
        if (trimmedKey.isEmpty()) {
            throw new IllegalStateException("❌ JWT 시크릿 키가 설정되지 않았습니다.");
        }
        try {
            byte[] keyBytes = Decoders.BASE64.decode(trimmedKey);
            if (keyBytes.length < 32) {
                throw new IllegalStateException("❌ Base64 디코딩된 JWT 키는 최소 32바이트 이상이어야 합니다.");
            }
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("❌ JWT 시크릿 키가 올바른 Base64 형식이 아닙니다.", e);
        }
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
            logger.warn("🚫 블랙리스트된 토큰입니다: {}", token);
            return false;
        }
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token.trim());
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("❌ JWT 만료됨: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("❌ 지원되지 않는 JWT 형식: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("❌ JWT 형식이 올바르지 않음: {}", e.getMessage());
        } catch (SecurityException e) {
            logger.error("❌ JWT 서명 검증 실패: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("❌ JWT가 비어 있음: {}", e.getMessage());
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
            logger.error("❌ JWT에서 이메일 추출 중 오류 발생: {}", e.getMessage());
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
            logger.error("❌ JWT에서 역할 추출 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    public void invalidateToken(String token) {
        blacklistedTokens.add(token);
        logger.info("🚫 토큰 블랙리스트 추가됨: {}", token);
    }
}
