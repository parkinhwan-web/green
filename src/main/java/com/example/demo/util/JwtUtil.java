package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // ✅ 강력한 SecretKey를 자동 생성
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // ✅ SecretKey 반환
    private SecretKey getSigningKey() {
        return SECRET_KEY;
    }

    // ✅ JWT 토큰 생성 (userId, username 포함)
    public String generateToken(String email, Long userId, String username) {
        return Jwts.builder()
                .setSubject(email)  // 주체: 이메일
                .claim("userId", userId)        // 사용자 ID 포함
                .claim("username", username)    // 사용자 이름 포함
                .setIssuedAt(new Date())        // 발급일
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1시간 유효
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ JWT에서 클레임 전체 추출
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ 이메일(Subject) 추출
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // ✅ 사용자 ID 추출
    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    // ✅ 사용자 이름 추출
    public String extractUsername(String token) {
        return extractClaims(token).get("username", String.class);
    }
}
