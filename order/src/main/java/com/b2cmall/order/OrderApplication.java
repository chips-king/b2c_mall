package com.b2cmall.order;

import com.b2cmall.common.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/** 订单独立存储；商品和员工通过Feign按服务名查询。 */
@SpringBootApplication
@EnableFeignClients
@Import(GlobalExceptionHandler.class)
public class OrderApplication {
    public static void main(String[] args) { SpringApplication.run(OrderApplication.class, args); }
}
