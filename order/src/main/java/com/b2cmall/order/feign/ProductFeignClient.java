package com.b2cmall.order.feign;

import com.b2cmall.common.response.BaseResponseVO;
import com.b2cmall.order.feign.response.ProductVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "product-service", path = "/product")
public interface ProductFeignClient {
    @GetMapping("/{id}")
    BaseResponseVO<ProductVO> get(@PathVariable("id") long id, @RequestHeader("Authorization") String token);
}
