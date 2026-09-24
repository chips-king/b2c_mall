package com.b2cmall.employee.config;

import com.b2cmall.common.auth.JwtTokenService;
import com.b2cmall.employee.dao.mapper.EmployeeMapper;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class EmployeeAuthConfig {
    @Bean
    public JwtTokenService jwtTokenService(@Value("${mall.auth.jwt-secret-base64}") String secret,
                                           @Value("${mall.auth.token-ttl-seconds}") long ttl) {
        return new JwtTokenService(secret, ttl, Clock.systemUTC());
    }
    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public ApplicationRunner fillDefaultAvatars(EmployeeMapper mapper,
            @Value("${mall.auth.default-avatar}") String avatar) {
        if (avatar == null || avatar.isBlank()) { throw new IllegalArgumentException("默认头像不能为空"); }
        // 仅补齐已有员工的空头像，保留已设置的个人头像。
        return args -> mapper.fillDefaultAvatars(avatar);
    }
}
