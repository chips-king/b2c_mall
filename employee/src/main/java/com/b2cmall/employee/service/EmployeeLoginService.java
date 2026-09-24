package com.b2cmall.employee.service;

import com.b2cmall.common.auth.AuthenticatedEmployee;
import com.b2cmall.common.auth.JwtTokenService;
import com.b2cmall.common.exception.BusinessException;
import com.b2cmall.employee.dao.mapper.EmployeeMapper;
import com.b2cmall.employee.dao.po.EmployeePO;
import com.b2cmall.employee.web.request.LoginRequestVO;
import com.b2cmall.employee.web.response.EmployeeInfoVO;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** 登录按查询、密码校验、JWT、Redis、成功审计执行；远程Redis操作不占用SQLite事务。 */
@Service
public class EmployeeLoginService {
    private final EmployeeMapper employees;
    private final PasswordEncoder encoder;
    private final JwtTokenService jwt;
    private final RedisTokenStore tokens;
    private final LoginAuditService audits;
    public EmployeeLoginService(EmployeeMapper employees, PasswordEncoder encoder, JwtTokenService jwt,
                                RedisTokenStore tokens, LoginAuditService audits) {
        this.employees = employees; this.encoder = encoder; this.jwt = jwt; this.tokens = tokens; this.audits = audits;
    }

    public String login(LoginRequestVO request, String remoteAddress) {
        EmployeePO employee = employees.getEmployee(request.getShopId(), request.getUsername());
        if (employee == null || !encoder.matches(request.getPassword(), employee.getPassword())) {
            audits.failure(request.getShopId(), employee == null ? null : employee.getId(),
                    request.getUsername(), remoteAddress, "BAD_CREDENTIALS");
            throw new BusinessException(401, "员工账号或密码错误");
        }
        if (!Integer.valueOf(1).equals(employee.getStatus())) {
            audits.failure(employee.getShopId(), employee.getId(), employee.getUsername(), remoteAddress, "DISABLED");
            throw new BusinessException(401, "员工账号或密码错误");
        }
        AuthenticatedEmployee identity = new AuthenticatedEmployee(employee.getShopId(), employee.getId(), employee.getUsername());
        JwtTokenService.IssuedToken issued = jwt.issue(identity);
        try { tokens.save(identity, issued.token(), issued.expiresAt()); }
        catch (DataAccessException exception) {
            audits.failure(employee.getShopId(), employee.getId(), employee.getUsername(), remoteAddress, "SESSION_UNAVAILABLE");
            throw unavailable();
        }
        try { audits.success(employee, remoteAddress); }
        catch (RuntimeException exception) {
            // 两种存储不构成分布式事务；SQL失败时只撤销本次token，绝不覆盖或删除后来的登录。
            try { tokens.removeIfSame(identity, issued.token()); }
            catch (RuntimeException cleanupFailure) { exception.addSuppressed(cleanupFailure); }
            throw exception;
        }
        return issued.token();
    }

    public boolean checkToken(String token) { authenticate(token); return true; }

    public EmployeeInfoVO currentEmployee(String token) {
        EmployeePO employee = authenticate(token);
        return new EmployeeInfoVO(employee.getId(), employee.getShopId(), employee.getUsername(),
                employee.getAvatarUrl(), employee.getLoginCount(), employee.getLastLoginTime());
    }

    public void logout(String token) {
        EmployeePO employee = authenticate(token);
        try {
            if (!tokens.removeIfSame(new AuthenticatedEmployee(employee.getShopId(), employee.getId(), employee.getUsername()), token)) {
                throw new BusinessException(401, "登录会话已失效");
            }
        } catch (DataAccessException exception) { throw unavailable(); }
    }

    private EmployeePO authenticate(String token) {
        AuthenticatedEmployee identity = jwt.verify(token);
        try {
            if (!tokens.matches(identity, token)) { throw new BusinessException(401, "登录会话已失效"); }
        } catch (DataAccessException exception) { throw unavailable(); }
        EmployeePO employee = employees.findById(identity.shopId(), identity.userId());
        if (employee == null || !Integer.valueOf(1).equals(employee.getStatus()) || !employee.getUsername().equals(identity.username())) {
            throw new BusinessException(401, "员工身份已失效");
        }
        return employee;
    }
    private BusinessException unavailable() { return new BusinessException(503, "登录会话服务暂不可用"); }
}
