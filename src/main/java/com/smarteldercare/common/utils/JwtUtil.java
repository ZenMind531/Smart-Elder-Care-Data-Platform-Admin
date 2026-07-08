package com.smarteldercare.common.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    // 密钥（用来签名，保证 token 无法被伪造）
    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(

                    "smart-elder-care-jwt-secret-key-2026".getBytes()
            );

    // 有效期：24小时
    private static final long EXPIRE = 24 * 60 * 60 *
            1000;

    // 生成 token
    public static String generate(Long userId, String
            username) {
        return Jwts.builder()
                .subject(userId.toString())        // 存用户ID
                .claim("username", username)       // 存用户名
                .issuedAt(new Date())              // 签发时间
                .expiration(new
                        Date(System.currentTimeMillis() + EXPIRE)) // 过期时间
                .signWith(KEY)                     // 签名
                .compact();
    }
    //解析token
    public static Long getUserId(String token) {
        return Long.valueOf(
                Jwts.parser()
                        .verifyWith(KEY)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject()
        );
    }
    public static String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }
}