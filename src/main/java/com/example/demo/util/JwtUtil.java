package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret; // application.properties에서 불러오기

    private SecretKey secretKey;

    // ✅ 애플리케이션 시작 시 시크릿 키 초기화
    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getEncoder().encode(secret.getBytes());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // ✅ 토큰 생성 (이메일, ID, 사용자명 포함)
    public String generateToken(String email, Long userId, String username) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("username", username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1시간
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ 클레임 전체 추출
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ 개별 클레임 추출
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    public String extractUsername(String token) {
        return extractClaims(token).get("username", String.class);
    }
}
