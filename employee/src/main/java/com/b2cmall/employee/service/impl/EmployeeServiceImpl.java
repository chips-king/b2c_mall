package com.b2cmall.employee.service.impl;

import com.b2cmall.common.exception.BusinessException;
import com.b2cmall.employee.dao.mapper.EmployeeMapper;
import com.b2cmall.employee.dao.po.EmployeePO;
import com.b2cmall.employee.service.EmployeeService;
import com.b2cmall.employee.web.request.AddEmployeeRequestVO;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeMapper mapper;

    private final String defaultAvatar;

    public EmployeeServiceImpl(EmployeeMapper mapper,
            @Value("${mall.auth.default-avatar:/employee/images/default-avatar.svg}") String defaultAvatar) {
        if (defaultAvatar == null || defaultAvatar.isBlank()) { throw new IllegalArgumentException("默认头像不能为空"); }
        this.mapper = mapper;
        this.defaultAvatar = defaultAvatar;
    }

    @Override
    @Transactional
    public Long addEmployee(AddEmployeeRequestVO request) {
        EmployeePO employee = new EmployeePO();
        employee.setShopId(request.getShopId());
        employee.setUsername(request.getUsername());
        employee.setAvatarUrl(defaultAvatar);
        // 哈希由 Shop 首次注册时生成，直接保存；再次 BCrypt 会导致原密码无法校验。
        employee.setPassword(request.getPasswordHash());
        int rows = mapper.save(employee);
        if (rows == 1) {
            if (employee.getId() == null || employee.getId() <= 0) {
                throw new IllegalStateException("保存员工后未获得有效主键");
            }
            return employee.getId();
        }
        if (rows != 0) { throw new IllegalStateException("员工写入行数异常"); }

        // 返回同一记录的真实ID；同账号不同凭据不是幂等重放，禁止覆盖。
        EmployeePO existing = mapper.getEmployee(request.getShopId(), request.getUsername());
        if (existing == null) { throw new IllegalStateException("员工冲突后记录不存在"); }
        if (!existing.getPassword().equals(request.getPasswordHash())) {
            throw new BusinessException(409, "该店铺员工账号已存在且初始化凭据不一致");
        }
        return existing.getId();
    }
}
