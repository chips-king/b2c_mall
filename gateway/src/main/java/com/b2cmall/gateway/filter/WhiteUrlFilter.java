package com.b2cmall.gateway.filter;

import java.util.regex.Pattern;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** 只标记公开入口，仍继续经过鉴权过滤器的路径检查与身份头清理。 */
@Component
public class WhiteUrlFilter implements GlobalFilter, Ordered {
    static final String PUBLIC_ENTRY = WhiteUrlFilter.class.getName() + ".publicEntry";
    private static final Pattern INITIALIZATION = Pattern.compile("/api/shop/registration/status");
    private static final Pattern RETRY = Pattern.compile("/api/shop/registration/retry");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getRawPath();
        String method = exchange.getRequest().getMethodValue();
        boolean publicEntry = ("POST".equals(method) && ((path.equals("/api/shop/register") || path.equals("/api/shop/v2/register"))
                || path.equals("/employee/login") || RETRY.matcher(path).matches()))
                || ("GET".equals(method) && (INITIALIZATION.matcher(path).matches()
                || path.equals("/employee/images/default-avatar.svg")));
        // 每个请求重新计算内部属性，不读取客户端提供的放行标记；方法和完整路径必须同时匹配。
        exchange.getAttributes().put(PUBLIC_ENTRY, publicEntry);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() { return Ordered.HIGHEST_PRECEDENCE + 1; }
}
