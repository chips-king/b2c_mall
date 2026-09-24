package com.b2cmall.employee.web.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

/** 仅供受保护的服务间初始化接口使用，明确区分哈希和前台注册的明文密码。 */
public class AddEmployeeRequestVO {
    @NotNull(message = "店铺ID不能为空")
    @Positive(message = "店铺ID必须为正数")
    private Long shopId;
    @NotBlank(message = "员工账号不能为空")
    private String username;
    @NotBlank(message = "密码哈希不能为空")
    @Pattern(regexp = "\\A\\$2[aby]\\$(0[4-9]|[12][0-9]|3[01])\\$[./A-Za-z0-9]{53}\\z", message = "密码哈希必须为BCrypt格式")
    private String passwordHash;

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username == null ? null : username.strip(); }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
