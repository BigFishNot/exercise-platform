package com.exercise.campus.component;

import com.exercise.campus.exception.BusinessException;
import com.exercise.campus.enums.ResponseCodeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录上下文持有器（基于 ThreadLocal）
 * 业务代码取当前登录用户只能从这里取，禁止在 Controller / Service 里再次解析 token
 */
public class LoginContextHolder {

    private static final ThreadLocal<Map<String, Object>> CTX = new ThreadLocal<>();

    private static final String KEY_USER_ID = "userId";
    private static final String KEY_ROLE_TYPE = "roleType";
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_NICK_NAME = "nickName";

    public static void set(String userId, Integer roleType, String account, String nickName) {
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_USER_ID, userId);
        map.put(KEY_ROLE_TYPE, roleType);
        map.put(KEY_ACCOUNT, account);
        map.put(KEY_NICK_NAME, nickName);
        CTX.set(map);
    }

    public static void clear() {
        CTX.remove();
    }

    public static String requireUserId() {
        String userId = getUserId();
        if (userId == null) {
            throw new BusinessException(ResponseCodeEnum.UNAUTHORIZED);
        }
        return userId;
    }

    public static Integer requireRoleType() {
        Integer roleType = getRoleType();
        if (roleType == null) {
            throw new BusinessException(ResponseCodeEnum.UNAUTHORIZED);
        }
        return roleType;
    }

    public static String getUserId() {
        return getString(KEY_USER_ID);
    }

    public static Integer getRoleType() {
        Object v = get(KEY_ROLE_TYPE);
        return v == null ? null : (Integer) v;
    }

    public static String getAccount() {
        return getString(KEY_ACCOUNT);
    }

    public static String getNickName() {
        return getString(KEY_NICK_NAME);
    }

    private static Object get(String key) {
        Map<String, Object> map = CTX.get();
        return map == null ? null : map.get(key);
    }

    private static String getString(String key) {
        Object v = get(key);
        return v == null ? null : v.toString();
    }
}