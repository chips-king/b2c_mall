package com.b2cmall.gateway.service;

import com.b2cmall.common.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/** 只接受员工服务明确确认的有效会话；超时、异常和不完整响应都不能放行。 */
public class EmployeeAuthClient {
    private final WebClient client;
    private final Duration timeout;

    public EmployeeAuthClient(WebClient client, Duration timeout) {
        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("网关鉴权超时必须大于零");
        }
        this.client = client;
        this.timeout = timeout;
    }

    public Mono<Void> checkToken(String token) {
        // 目标固定为服务名而非请求头或用户参数，避免把认证头发送到外部地址。
        return Mono.defer(() -> client.post().uri("http://employee-service/employee/checkToken")
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchangeToMono(response -> {
                    if (response.rawStatusCode() == 401) {
                        return response.releaseBody().then(Mono.<JsonNode>error(unauthorized()));
                    }
                    if (response.rawStatusCode() != 200) {
                        return response.releaseBody().then(Mono.<JsonNode>error(unavailable()));
                    }
                    return response.bodyToMono(JsonNode.class).switchIfEmpty(Mono.error(unavailable()));
                })
                .flatMap(body -> {
                    if (!body.path("status").isIntegralNumber() || body.path("status").asLong() != 200
                            || !body.path("data").isBoolean()) {
                        return Mono.<Void>error(unavailable());
                    }
                    return body.path("data").booleanValue() ? Mono.<Void>empty() : Mono.error(unauthorized());
                }))
                .timeout(timeout)
                // 不保留可能包含请求认证头的客户端异常；只向调用方返回固定错误分类。
                .onErrorMap(error -> error instanceof BusinessException ? error : unavailable());
    }

    private BusinessException unauthorized() { return new BusinessException(401, "登录会话无效，请重新登录"); }
    private BusinessException unavailable() { return new BusinessException(503, "身份认证服务暂不可用"); }
}
