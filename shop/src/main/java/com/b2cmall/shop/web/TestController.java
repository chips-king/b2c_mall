package com.b2cmall.shop.web;

import com.b2cmall.shop.dao.mapper.DatabaseProbeMapper;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final DatabaseProbeMapper mapper;
    private final String serviceName;

    public TestController(DatabaseProbeMapper mapper,
                          @Value("${spring.application.name}") String serviceName) {
        this.mapper = mapper;
        this.serviceName = serviceName;
    }

    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of("service", serviceName);
    }

    @GetMapping("/test/db")
    public Map<String, Object> database() {
        // 结果直接来自数据库执行；连接或 SQL 失败交由框架返回错误。
        return mapper.probe();
    }
}
