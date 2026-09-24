package com.b2cmall.gateway.filter;

import com.b2cmall.common.exception.BusinessException;
import com.b2cmall.common.response.BaseResponseVO;
import com.b2cmall.gateway.service.EmployeeAuthClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** 在路径改写及转发前鉴权；所有已匹配路由默认受保护，公开入口必须精确匹配。 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    private static final Set<String> EMPLOYEE_PATHS = Set.of("/employee/login", "/employee/checkToken",
            "/employee/me", "/employee/logout", "/employee/images/default-avatar.svg");
    private final EmployeeAuthClient auth;
    private final ObjectMapper json;

    public AuthFilter(EmployeeAuthClient auth, ObjectMapper json) {
        this.auth = auth; this.json = json;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getRawPath();
        // 拒绝可能被网关与Servlet容器解释成不同路径的写法，防止编码或路径参数绕过边界。
        if (path.contains("%") || path.contains(";") || path.contains("\\") || path.contains("//")
                || Pattern.compile("(^|/)\\.\\.?(/|$)").matcher(path).find()) {
            return reject(exchange, 400, "请求路径格式不合法");
        }
        if (path.startsWith("/employee/") && !EMPLOYEE_PATHS.contains(path)) {
            return reject(exchange, 403, "该员工接口不允许通过网关访问");
        }
        // 内部调用凭据与客户端自称的身份都不能穿透公网入口；业务身份继续由原始token确定。
        ServerWebExchange sanitized = exchange.mutate().request(request -> request.headers(headers -> {
            headers.remove("X-Internal-Token");
            headers.remove("X-Shop-Id");
            headers.remove("X-User-Id");
        })).build();
        if (Boolean.TRUE.equals(exchange.getAttribute(WhiteUrlFilter.PUBLIC_ENTRY))) { return chain.filter(sanitized); }
        List<String> tokens = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (tokens == null || tokens.size() != 1 || tokens.get(0).isBlank()
                || !tokens.get(0).equals(tokens.get(0).strip()) || tokens.get(0).contains(",")) {
            return reject(exchange, 401, "请提供有效的Authorization请求头");
        }
        // 错误转换只包住鉴权调用，下游业务异常不能被误报成身份认证失败。
        return auth.checkToken(tokens.get(0)).thenReturn(200)
                .onErrorResume(BusinessException.class, error -> Mono.just(error.getStatus()))
                .flatMap(status -> status == 200 ? chain.filter(sanitized)
                        : reject(exchange, status, status == 401 ? "登录会话无效，请重新登录" : "身份认证服务暂不可用"));
    }

    private Mono<Void> reject(ServerWebExchange exchange, int status, String message) {
        byte[] body;
        try { body = json.writeValueAsBytes(BaseResponseVO.failure(status, message)); }
        catch (JsonProcessingException error) { return Mono.error(error); }
        exchange.getResponse().setStatusCode(HttpStatus.valueOf(status));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
    }

    @Override
    public int getOrder() { return Ordered.HIGHEST_PRECEDENCE + 2; }
}
