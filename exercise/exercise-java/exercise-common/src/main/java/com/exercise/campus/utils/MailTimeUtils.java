package com.exercise.campus.utils;

import java.util.regex.Pattern;

/**
 * 邮件发送时间点工具
 * 格式: "HH:mm" 逗号分隔, 1-6 个
 */
public class MailTimeUtils {

    private static final Pattern P = Pattern.compile("^([01]?\\d|2[0-3]):[0-5]\\d$");

    public static boolean isValidTimePoints(String s) {
        if (s == null || s.isEmpty()) return true; // 允许空
        String[] arr = s.split(",");
        if (arr.length < 1 || arr.length > 6) return false;
        for (String t : arr) {
            if (!P.matcher(t.trim()).matches()) return false;
        }
        return true;
    }

    public static String[] parse(String s) {
        if (s == null || s.isEmpty()) return new String[0];
        return s.split(",");
    }
}