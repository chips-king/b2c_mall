package com.b2cmall.product.web.filter;

import com.b2cmall.common.auth.AuthenticatedEmployee;
import com.b2cmall.common.exception.BusinessException;
import com.b2cmall.common.response.BaseResponseVO;
import com.b2cmall.product.context.RequestIdentityContext;
import com.b2cmall.product.feign.EmployeeFeignClient;
import com.b2cmall.product.feign.response.EmployeeIdentityVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** 直连Product也必须验证会话，不能只解析JWT或相信客户端身份头。 */
@Component
public class ProductAuthenticationFilter extends OncePerRequestFilter {
    private final EmployeeFeignClient employees;
    private final ObjectMapper json;
    public ProductAuthenticationFilter(EmployeeFeignClient employees, ObjectMapper json) {
        this.employees = employees; this.json = json;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        RequestIdentityContext.clear();
        try {
            try { RequestIdentityContext.set(authenticate(request)); }
            catch (BusinessException error) {
                response.setStatus(error.getStatus());
                response.setContentType("application/json;charset=UTF-8");
                json.writeValue(response.getWriter(), BaseResponseVO.failure(error.getStatus(), error.getMessage()));
                return;
            }
            chain.doFilter(request, response);
        } finally {
            // 包括鉴权失败、参数错误及事务异常，所有退出路径均删除线程身份。
            RequestIdentityContext.clear();
        }
    }
    private AuthenticatedEmployee authenticate(HttpServletRequest request) {
        var headers = Collections.list(request.getHeaders("Authorization"));
        if (headers.size() != 1 || headers.get(0).isBlank() || headers.get(0).contains(",")) {
            throw new BusinessException(401, "请提供有效的Authorization请求头");
        }
        BaseResponseVO<EmployeeIdentityVO> result;
        try { result = employees.current(headers.get(0)); }
        catch (FeignException error) {
            // 不传播含请求信息的Feign异常；员工服务不可用时明确拒绝，不缓存身份继续放行。
            throw new BusinessException(error.status() == 401 ? 401 : 503,
                    error.status() == 401 ? "登录会话已失效" : "身份认证服务暂不可用");
        }
        if (result == null || result.getStatus() != 200 || result.getData() == null) {
            throw new BusinessException(503, "身份认证服务响应异常");
        }
        EmployeeIdentityVO data = result.getData();
        if (data.shopId() == null || data.shopId() <= 0 || data.userId() == null || data.userId() <= 0
                || data.username() == null || data.username().isBlank()) {
            throw new BusinessException(503, "身份认证服务响应异常");
        }
        return new AuthenticatedEmployee(data.shopId(), data.userId(), data.username());
    }
}
