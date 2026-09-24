package com.b2cmall.common.auth;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.b2cmall.common.exception.BusinessException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

/** 固定HS256并严格检查时间和身份声明；仅验签不能证明登录会话仍有效。 */
public final class JwtTokenService {
    public static final String ISSUER = "b2c_mall";
    private final byte[] key;
    private final long ttlSeconds;
    private final Clock clock;

    public JwtTokenService(String secretBase64, long ttlSeconds, Clock clock) {
        if (secretBase64 == null || secretBase64.isBlank()) { throw new IllegalArgumentException("JWT密钥必须配置"); }
        try { key = Base64.getDecoder().decode(secretBase64); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("JWT密钥必须为Base64编码"); }
        if (key.length < 32 || ttlSeconds <= 0) { throw new IllegalArgumentException("JWT密钥至少32字节且有效期必须大于零"); }
        this.ttlSeconds = ttlSeconds;
        this.clock = Objects.requireNonNull(clock);
    }

    public record IssuedToken(String token, Instant expiresAt) { }

    public IssuedToken issue(AuthenticatedEmployee identity) {
        long issuedAt = clock.instant().getEpochSecond();
        long expiresAt = Math.addExact(issuedAt, ttlSeconds);
        String token = JWT.create().setSigner("HS256", key)
                .setPayload("iss", ISSUER).setPayload("iat", issuedAt).setPayload("nbf", issuedAt)
                .setPayload("exp", expiresAt).setPayload("jti", UUID.randomUUID().toString())
                .setPayload("shopId", identity.shopId()).setPayload("userId", identity.userId())
                .setPayload("username", identity.username()).sign();
        return new IssuedToken(token, Instant.ofEpochSecond(expiresAt));
    }

    public AuthenticatedEmployee verify(String token) {
        try {
            if (token == null || token.isBlank() || !token.equals(token.strip())) { throw unauthorized(); }
            JWT jwt = JWT.of(token);
            // 不允许根据用户可控的alg头选择算法，拒绝none及其他签名算法。
            if (!"HS256".equals(jwt.getAlgorithm()) || !jwt.verify(JWTSignerUtil.hs256(key))) { throw unauthorized(); }
            if (!ISSUER.equals(jwt.getPayload("iss"))) { throw unauthorized(); }
            long now = clock.instant().getEpochSecond();
            long issuedAt = number(jwt, "iat");
            long notBefore = number(jwt, "nbf");
            long expiresAt = number(jwt, "exp");
            if (issuedAt > now || notBefore > now || expiresAt <= now || expiresAt <= issuedAt) { throw unauthorized(); }
            text(jwt, "jti");
            return new AuthenticatedEmployee(number(jwt, "shopId"), number(jwt, "userId"), text(jwt, "username"));
        } catch (RuntimeException exception) {
            // 解析异常可能包含原token；统一安全错误，不把原异常交给日志或HTTP响应。
            throw unauthorized();
        }
    }

    private long number(JWT jwt, String name) {
        Object value = jwt.getPayload(name);
        if (!(value instanceof Number)) { throw unauthorized(); }
        return new BigDecimal(value.toString()).longValueExact();
    }
    private String text(JWT jwt, String name) {
        Object value = jwt.getPayload(name);
        if (!(value instanceof String text) || text.isBlank()) { throw unauthorized(); }
        return text;
    }
    private BusinessException unauthorized() { return new BusinessException(401, "登录凭证无效或已过期"); }
}
