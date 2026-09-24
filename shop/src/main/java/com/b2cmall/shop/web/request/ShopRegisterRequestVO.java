package com.b2cmall.shop.web.request;

import java.nio.charset.StandardCharsets;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/** 课堂注册字段；名称和账号规范化，密码保持用户输入原样。 */
public class ShopRegisterRequestVO {
    public static final int MAX_SHOP_NAME_LENGTH = 6;
    public static final int MAX_PASSWORD_BYTES = 72;

    @NotBlank(message = "店铺名不能为空")
    @Size(max = MAX_SHOP_NAME_LENGTH, message = "店铺名不能超过6个字符")
    private String shopName;

    @NotBlank(message = "管理员账号不能为空")
    private String adminAccount;

    @NotBlank(message = "管理员密码不能为空")
    private String adminPassword;

    // BCrypt 按字节处理密码；多字节字符不能仅靠 Java 字符长度校验。
    @AssertTrue(message = "管理员密码的UTF-8长度不能超过72字节")
    public boolean isPasswordWithinByteLimit() {
        return adminPassword == null
                || adminPassword.getBytes(StandardCharsets.UTF_8).length <= MAX_PASSWORD_BYTES;
    }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName == null ? null : shopName.strip(); }
    public String getAdminAccount() { return adminAccount; }
    public void setAdminAccount(String adminAccount) { this.adminAccount = adminAccount == null ? null : adminAccount.strip(); }
    public String getAdminPassword() { return adminPassword; }
    public void setAdminPassword(String adminPassword) { this.adminPassword = adminPassword; }
}
