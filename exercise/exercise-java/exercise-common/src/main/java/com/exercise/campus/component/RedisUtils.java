package com.exercise.campus.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 通用 Redis 工具（原子计数、过期、删除）
 * 仅依赖 StringRedisTemplate
 */
@Component
public class RedisUtils {

    @Autowired
    private StringRedisTemplate redis;

    /**
     * 原子自增并设置过期（首次 incr 时设置 TTL）
     * @return 自增后的值；key 不存在返回 null 表示失败
     */
    public Long incr(String key, long delta, long ttlSeconds) {
        try {
            Long val = redis.opsForValue().increment(key, delta);
            if (val != null && val == delta) {
                redis.expire(key, ttlSeconds, TimeUnit.SECONDS);
            }
            return val;
        } catch (Exception e) {
            return null;
        }
    }

    /** 自减 */
    public Long decr(String key, long delta) {
        try {
            return redis.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            return null;
        }
    }

    public void set(String key, String value, long ttlSeconds) {
        try {
            redis.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
        } catch (Exception ignored) {}
    }

    public String get(String key) {
        try {
            return redis.opsForValue().get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public void del(String key) {
        try {
            redis.delete(key);
        } catch (Exception ignored) {}
    }

    public Long getTtl(String key) {
        try {
            return redis.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }
}