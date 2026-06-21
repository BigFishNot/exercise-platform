package com.exercise.campus.component;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * Redis 登录态组件
 * 管理端 token 头 adminToken、用户端 token 头 studentToken
 * key: login:{token}，value: JSON 字符串（userId|roleType|account|nickName）
 */
@Component
public class LoginRedisComponent {

    /** 登录态默认有效期（小时） */
    public static final long LOGIN_TTL_HOURS = 8L;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static String keyAdmin(String token) {
        return "login:admin:" + token;
    }

    private static String keyUser(String token) {
        return "login:user:" + token;
    }

    public String createAdminToken(String userId, Integer roleType, String account, String nickName) {
        String token = UUID.randomUUID().toString().replace("-", "");
        CacheValue v = new CacheValue();
        v.userId = userId;
        v.roleType = roleType;
        v.account = account;
        v.nickName = nickName;
        stringRedisTemplate.opsForValue().set(keyAdmin(token), JsonUtil.toJson(v), Duration.ofHours(LOGIN_TTL_HOURS));
        return token;
    }

    public String createUserToken(String userId, Integer roleType, String account, String nickName) {
        String token = UUID.randomUUID().toString().replace("-", "");
        CacheValue v = new CacheValue();
        v.userId = userId;
        v.roleType = roleType;
        v.account = account;
        v.nickName = nickName;
        stringRedisTemplate.opsForValue().set(keyUser(token), JsonUtil.toJson(v), Duration.ofHours(LOGIN_TTL_HOURS));
        return token;
    }

    public CacheValue getAdmin(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        String json = stringRedisTemplate.opsForValue().get(keyAdmin(token));
        return json == null ? null : JsonUtil.fromJson(json, CacheValue.class);
    }

    public CacheValue getUser(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        String json = stringRedisTemplate.opsForValue().get(keyUser(token));
        return json == null ? null : JsonUtil.fromJson(json, CacheValue.class);
    }

    public void cleanAdmin(String token) {
        if (token != null) {
            stringRedisTemplate.delete(keyAdmin(token));
        }
    }

    public void cleanUser(String token) {
        if (token != null) {
            stringRedisTemplate.delete(keyUser(token));
        }
    }

    @Data
    public static class CacheValue {
        private String userId;
        private Integer roleType;
        private String account;
        private String nickName;
    }
}