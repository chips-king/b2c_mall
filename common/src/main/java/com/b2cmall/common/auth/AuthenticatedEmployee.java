package com.b2cmall.common.auth;

/** 只承载已验证的身份，不传递密码、哈希或会话密钥。 */
public record AuthenticatedEmployee(long shopId, long userId, String username) {
    public AuthenticatedEmployee {
        if (shopId <= 0 || userId <= 0 || username == null || username.isBlank()) {
            throw new IllegalArgumentException("员工身份无效");
        }
    }
}
