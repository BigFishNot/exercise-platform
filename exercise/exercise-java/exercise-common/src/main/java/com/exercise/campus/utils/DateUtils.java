package com.exercise.campus.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 通用日期工具
 */
public class DateUtils {

    /** GMT+8 时区常量 */
    public static final TimeZone GMT8 = TimeZone.getTimeZone("GMT+8");

    /** 含首尾的天数差（同日返回 0） */
    public static long daysBetween(Date start, Date end) {
        if (start == null || end == null) return 0;
        long ms = end.getTime() - start.getTime();
        if (ms < 0) return -1;
        return ms / (1000L * 60 * 60 * 24);
    }

    /** 服务器 GMT+8 当日 00:00:00 */
    public static Date todayGMT8() {
        Calendar c = Calendar.getInstance(GMT8);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /** 去掉时分秒，保留日期 */
    public static Date stripTime(Date d) {
        if (d == null) return null;
        Calendar c = Calendar.getInstance(GMT8);
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /** 比较两个日期是否同一天（按 GMT+8 截断到日） */
    public static boolean isSameDay(Date a, Date b) {
        if (a == null || b == null) return false;
        return stripTime(a).equals(stripTime(b));
    }
}