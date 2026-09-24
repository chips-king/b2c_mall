package com.b2cmall.employee;

import com.b2cmall.common.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/** 员工数据由本服务独立管理，Shop 只能通过内部 HTTP 契约初始化管理员。 */
@SpringBootApplication
@Import(GlobalExceptionHandler.class)
public class EmployeeApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmployeeApplication.class, args);
    }
}
