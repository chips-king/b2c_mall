package com.b2cmall.gateway.filter;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long started = System.nanoTime();
        String requestId = exchange.getRequest().getId();
        String path = exchange.getRequest().getPath().value();
        // 仅记录路径，不记录查询参数、请求体或认证头，避免泄露登录凭据。
        exchange.getResponse().beforeCommit(() -> {
            log.info("requestId={} method={} path={} status={} elapsedMs={}",
                    requestId, exchange.getRequest().getMethod(), path,
                    exchange.getResponse().getRawStatusCode(),
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
            return Mono.empty();
        });
        // 异常继续向外传播，由框架生成失败响应；不可转换为成功结果。
        return chain.filter(exchange).doOnError(error ->
                log.warn("requestId={} path={} failure={}", requestId, path, error.getClass().getSimpleName()));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
