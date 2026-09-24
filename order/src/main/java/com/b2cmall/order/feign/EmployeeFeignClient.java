package com.b2cmall.order.feign;

import com.b2cmall.common.response.BaseResponseVO;
import com.b2cmall.order.feign.response.EmployeeIdentityVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "employee-service", path = "/employee")
public interface EmployeeFeignClient {
    @GetMapping("/me")
    BaseResponseVO<EmployeeIdentityVO> current(@RequestHeader("Authorization") String token);
}
