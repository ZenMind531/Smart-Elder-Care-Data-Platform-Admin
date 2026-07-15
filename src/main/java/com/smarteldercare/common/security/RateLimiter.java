package com.smarteldercare.common.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 登录限流器（Redis 版）。
 * 5 分钟内密码错误 5 次 → 锁定 10 分钟。
 * 多实例部署共享同一 Redis，限流全局生效。
 */
@Component
public class RateLimiter {

    private static final int MAX_ATTEMPTS = 5;           // 最多尝试次数
    private static final long WINDOW_SECONDS = 5 * 60;   // 统计窗口 5 分钟
    private static final long LOCK_SECONDS = 10 * 60;    // 锁定 10 分钟

    private final StringRedisTemplate redis;

    public RateLimiter(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /** 登录前检查是否处于锁定期。 */
    public void checkLocked(String key) {
        String lockKey = key + ":lock";
        Long lockTtl = redis.getExpire(lockKey, TimeUnit.SECONDS);
        if (lockTtl != null && lockTtl > 0) {
            throw new TooManyException("尝试次数过多，请 " + lockTtl + " 秒后再试");
        }
    }

    /** 登录失败后记录一次失败。 */
    public void recordFailure(String key) {
        String countKey = key + ":count";
        Long count = redis.opsForValue().increment(countKey);
        if (count != null && count == 1) {
            redis.expire(countKey, WINDOW_SECONDS, TimeUnit.SECONDS);
        }

        if (count != null && count >= MAX_ATTEMPTS) {
            String lockKey = key + ":lock";
            redis.opsForValue().set(lockKey, "1", LOCK_SECONDS, TimeUnit.SECONDS);
            redis.delete(countKey);
            throw new TooManyException("尝试次数过多，已锁定 10 分钟");
        }
    }

    /** 登录成功后清零 */
    public void clear(String key) {
        redis.delete(key + ":count");
        redis.delete(key + ":lock");
    }

    /** 限流异常 */
    public static class TooManyException extends RuntimeException {
        public TooManyException(String msg) {
            super(msg);
        }
    }
}
