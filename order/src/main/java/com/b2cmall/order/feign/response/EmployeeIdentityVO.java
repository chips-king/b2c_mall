package com.b2cmall.order.feign.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 仅接收员工服务验证后的身份，忽略头像、登录次数等本模块无需使用的信息。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EmployeeIdentityVO(Long shopId, Long userId, String username) { }
