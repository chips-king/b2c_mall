package com.b2cmall.shop.feign;

import com.b2cmall.common.response.BaseResponseVO;
import com.b2cmall.shop.feign.request.AddEmployeeRequestVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "employee-service", path = "/employee")
public interface EmployeeFeignClient {
    @PostMapping("/save")
    BaseResponseVO<Long> save(@RequestHeader("X-Internal-Token") String token,
                              @RequestBody AddEmployeeRequestVO request);
}
