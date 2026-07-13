package com.smarteldercare.common.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * 密码哈希工具（BCrypt，自带盐）。
 * 同一个密码每次哈希结果都不同，但能正确验证。
 */
public final class BCryptUtil {

    private BCryptUtil() {}

    private static final int COST = 12;

    public static String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(COST));
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(rawPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
