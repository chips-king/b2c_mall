package com.b2cmall.employee.web.response;

/** 响应显式列出可公开字段，避免直接序列化包含密码哈希的EmployeePO。 */
public record EmployeeInfoVO(Long userId, Long shopId, String username, String avatarUrl,
                             Integer loginCount, String lastLoginTime) { }
