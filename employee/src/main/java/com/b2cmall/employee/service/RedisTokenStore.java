package com.b2cmall.employee.service;

import com.b2cmall.common.auth.AuthenticatedEmployee;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

/** Redis保存每位员工当前的完整token；键存在并不足以证明传入token仍是当前会话。 */
@Service
public class RedisTokenStore {
    private static final DefaultRedisScript<Long> REMOVE_IF_SAME = new DefaultRedisScript<>(
            "if redis.call('GET', KEYS[1]) == ARGV[1] then return redis.call('DEL', KEYS[1]) else return 0 end", Long.class);
    private final StringRedisTemplate redis;
    private final String prefix;

    public RedisTokenStore(StringRedisTemplate redis, @Value("${mall.auth.redis-key-prefix}") String prefix) {
        if (prefix == null || prefix.isBlank() || !prefix.endsWith(":")) {
            throw new IllegalArgumentException("Redis项目前缀不能为空且须以冒号结尾");
        }
        this.redis = redis;
        this.prefix = prefix;
    }
    public void save(AuthenticatedEmployee identity, String token, Instant expiresAt) {
        Duration ttl = Duration.between(Instant.now(), expiresAt);
        if (ttl.isNegative() || ttl.isZero()) { throw new IllegalStateException("会话保存前token已过期"); }
        redis.opsForValue().set(key(identity), token, ttl);
    }
    public boolean matches(AuthenticatedEmployee identity, String token) {
        String current = redis.opsForValue().get(key(identity));
        return current != null && MessageDigest.isEqual(current.getBytes(StandardCharsets.UTF_8), token.getBytes(StandardCharsets.UTF_8));
    }
    public boolean removeIfSame(AuthenticatedEmployee identity, String token) {
        // 比较与删除在Redis内原子执行，旧会话退出或失败补偿不能删除并发创建的新会话。
        Long removed = redis.execute(REMOVE_IF_SAME, List.of(key(identity)), token);
        if (removed == null) { throw new IllegalStateException("Redis未返回会话删除结果"); }
        return removed == 1L;
    }
    private String key(AuthenticatedEmployee identity) {
        return prefix + "token:" + identity.shopId() + ":" + identity.userId();
    }
}
