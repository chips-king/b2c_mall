package com.b2cmall.gateway.config;

import com.b2cmall.gateway.service.EmployeeAuthClient;
import io.netty.channel.ChannelOption;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/** 使用响应式服务发现调用员工服务，鉴权网络等待不阻塞网关事件线程。 */
@Configuration
public class GatewayAuthConfig {
    @Bean
    @LoadBalanced
    public WebClient.Builder employeeAuthWebClientBuilder() { return WebClient.builder(); }

    @Bean
    public EmployeeAuthClient employeeAuthClient(
            @Qualifier("employeeAuthWebClientBuilder") WebClient.Builder builder,
            @Value("${mall.auth.timeout-ms}") int timeoutMs) {
        if (timeoutMs <= 0) { throw new IllegalArgumentException("网关鉴权超时必须大于零"); }
        Duration timeout = Duration.ofMillis(timeoutMs);
        HttpClient http = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutMs)
                .responseTimeout(timeout);
        return new EmployeeAuthClient(builder.clientConnector(new ReactorClientHttpConnector(http)).build(), timeout);
    }
}
