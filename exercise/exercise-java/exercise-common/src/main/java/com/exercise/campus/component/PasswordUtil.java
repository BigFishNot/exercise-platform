package com.exercise.campus.component;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * 密码工具
 * BCrypt 加密存储；登录时 BCrypt.checkpw 校验
 * 校验：必须同时包含字母和数字，长度 8-32
 */
public class PasswordUtil {

    private static final String LETTER_REGEX = ".*[A-Za-z].*";
    private static final String DIGIT_REGEX = ".*\\d.*";

    public static String encrypt(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return BCrypt.hashpw(raw, BCrypt.gensalt(10));
    }

    public static boolean matches(String raw, String hashed) {
        if (raw == null || hashed == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(raw, hashed);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidFormat(String raw) {
        if (raw == null) {
            return false;
        }
        int len = raw.length();
        if (len < 8 || len > 32) {
            return false;
        }
        return raw.matches(LETTER_REGEX) && raw.matches(DIGIT_REGEX);
    }
}