package com.b2cmall.product;

import com.b2cmall.common.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/** 商品服务使用独立数据库，员工身份通过服务调用确认。 */
@SpringBootApplication
@EnableFeignClients
@Import(GlobalExceptionHandler.class)
public class ProductApplication {
    public static void main(String[] args) { SpringApplication.run(ProductApplication.class, args); }
}
