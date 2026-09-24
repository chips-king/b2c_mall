package com.b2cmall.employee.service;

import com.b2cmall.common.exception.BusinessException;
import com.b2cmall.employee.dao.mapper.EmployeeMapper;
import com.b2cmall.employee.dao.mapper.LoginAuditMapper;
import com.b2cmall.employee.dao.po.EmployeePO;
import com.b2cmall.employee.dao.po.LoginAuditPO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginAuditService {
    private final EmployeeMapper employees;
    private final LoginAuditMapper audits;
    public LoginAuditService(EmployeeMapper employees, LoginAuditMapper audits) {
        this.employees = employees;
        this.audits = audits;
    }
    @Transactional
    public void success(EmployeePO employee, String remoteAddress) {
        if (employees.recordSuccessfulLogin(employee.getShopId(), employee.getId(), employee.getPassword()) != 1) {
            throw new BusinessException(401, "员工状态已变化，请重新登录");
        }
        // 次数、时间及成功审计一起提交；审计写入失败时统计同步回滚。
        insert(employee.getShopId(), employee.getId(), employee.getUsername(), remoteAddress, "SUCCESS", "SUCCESS");
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failure(Long shopId, Long employeeId, String username, String remoteAddress, String reason) {
        insert(shopId, employeeId, username, remoteAddress, "FAILURE", reason);
    }
    private void insert(Long shopId, Long employeeId, String username, String remoteAddress, String outcome, String reason) {
        LoginAuditPO audit = new LoginAuditPO();
        audit.setShopId(shopId); audit.setEmployeeId(employeeId); audit.setUsername(username);
        audit.setRemoteAddress(remoteAddress); audit.setOutcome(outcome); audit.setReasonCode(reason);
        if (audits.insert(audit) != 1 || audit.getId() == null || audit.getId() <= 0) {
            throw new IllegalStateException("登录审计保存失败");
        }
    }
}
