package com.b2cmall.employee.dao.mapper;

import com.b2cmall.employee.dao.po.LoginAuditPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginAuditMapper { int insert(LoginAuditPO audit); }
