package com.b2cmall.employee.web;

import com.b2cmall.common.exception.BusinessException;
import com.b2cmall.common.response.BaseResponseVO;
import com.b2cmall.employee.service.EmployeeService;
import com.b2cmall.employee.service.EmployeeLoginService;
import com.b2cmall.employee.web.request.LoginRequestVO;
import com.b2cmall.employee.web.response.EmployeeInfoVO;
import javax.servlet.http.HttpServletRequest;
import com.b2cmall.employee.web.request.AddEmployeeRequestVO;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/** 初始化接口是服务间权限边界；普通浏览器请求不能提交哈希创建员工。 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService service;
    private final EmployeeLoginService loginService;
    private final byte[] internalToken;

    public EmployeeController(EmployeeService service, EmployeeLoginService loginService,
                              @Value("${mall.internal-service-token}") String internalToken) {
        if (internalToken == null || internalToken.isBlank()) {
            throw new IllegalArgumentException("INTERNAL_SERVICE_TOKEN 不能为空");
        }
        this.service = service;
        this.loginService = loginService;
        this.internalToken = internalToken.getBytes(StandardCharsets.UTF_8);
    }

    @PostMapping("/save")
    public BaseResponseVO<Long> save(
            @RequestHeader(value = "X-Internal-Token", required = false) String suppliedToken,
            @RequestBody @Valid AddEmployeeRequestVO request) {
        if (suppliedToken == null || !MessageDigest.isEqual(internalToken, suppliedToken.getBytes(StandardCharsets.UTF_8))) {
            throw new BusinessException(401, "内部服务认证失败");
        }
        return BaseResponseVO.success(service.addEmployee(request));
    }
    @PostMapping("/login")
    public BaseResponseVO<String> login(@RequestBody @Valid LoginRequestVO request, HttpServletRequest servletRequest) {
        // 直接连接地址来自容器；不信任客户端自行提供的转发IP头。
        return BaseResponseVO.success(loginService.login(request, servletRequest.getRemoteAddr()));
    }

    @PostMapping("/checkToken")
    public BaseResponseVO<Boolean> checkToken(@RequestHeader(value = "Authorization", required = false) String token) {
        return BaseResponseVO.success(loginService.checkToken(token));
    }

    @GetMapping("/me")
    public BaseResponseVO<EmployeeInfoVO> me(@RequestHeader(value = "Authorization", required = false) String token) {
        return BaseResponseVO.success(loginService.currentEmployee(token));
    }

    @PostMapping("/logout")
    public BaseResponseVO<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        loginService.logout(token);
        return BaseResponseVO.success(null);
    }
}
