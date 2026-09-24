package com.b2cmall.employee.web.request;

import java.nio.charset.StandardCharsets;
import javax.validation.constraints.*;

public class LoginRequestVO {
    @NotNull(message = "店铺ID不能为空") @Positive(message = "店铺ID必须大于零")
    private Long shopId;
    @NotBlank(message = "员工账号不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @AssertTrue(message = "密码的UTF-8长度不能超过72字节")
    public boolean isPasswordWithinByteLimit() {
        return password == null || password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username == null ? null : username.strip(); }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
