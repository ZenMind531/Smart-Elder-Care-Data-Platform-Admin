package com.smarteldercare.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret:smart-elder-care-jwt-secret-key-2026}")
    private String secret;

    private static final long EXPIRE_MS = 24 * 60 * 60 * 1000;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** 生成 token */
    public String generate(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("username", username);
        claims.put("role", role);

        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(userId.toString())
                .issuedAt(new Date(now))
                .expiration(new Date(now + EXPIRE_MS))
                .signWith(getKey())
                .compact();
    }

    /** 生成 token（兼容旧调用方式，无 role） */
    public String generate(Long userId, String username) {
        return generate(userId, username, "unknown");
    }

    /** 解析 token，失败抛异常 */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
