package com.b2cmall.employee.service;

import com.b2cmall.employee.web.request.AddEmployeeRequestVO;

public interface EmployeeService {
    Long addEmployee(AddEmployeeRequestVO request);
}
