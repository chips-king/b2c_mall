package com.b2cmall.employee.dao.mapper;

import com.b2cmall.employee.dao.po.EmployeePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmployeeMapper {
    int save(EmployeePO employee);
    EmployeePO getEmployee(@Param("shopId") Long shopId, @Param("username") String username);
    EmployeePO findById(@Param("shopId") long shopId, @Param("userId") long userId);
    int recordSuccessfulLogin(@Param("shopId") long shopId, @Param("userId") long userId, @Param("expectedHash") String expectedHash);
    int fillDefaultAvatars(@Param("avatar") String avatar);
}

