package com.b2cmall.shop.web.request;

import java.nio.charset.StandardCharsets;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

/** 初始化阶段尚未登录，重试必须校验注册管理员凭据；密码不规范化、不记录日志。 */
public class RetryInitializationRequestVO {
    @NotBlank(message = "管理员账号不能为空")
    private String adminAccount;
    @NotBlank(message = "管理员密码不能为空")
    private String adminPassword;

    @AssertTrue(message = "管理员密码的UTF-8长度不能超过72字节")
    public boolean isPasswordWithinByteLimit() {
        return adminPassword == null || adminPassword.getBytes(StandardCharsets.UTF_8).length
                <= ShopRegisterRequestVO.MAX_PASSWORD_BYTES;
    }
    public String getAdminAccount() { return adminAccount; }
    public void setAdminAccount(String value) { adminAccount = value == null ? null : value.strip(); }
    public String getAdminPassword() { return adminPassword; }
    public void setAdminPassword(String value) { adminPassword = value; }
}
