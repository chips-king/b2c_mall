package com.b2cmall.shop.feign.request;

/** 与 Employee 的初始化契约对应；不实现含敏感哈希的 toString。 */
public class AddEmployeeRequestVO {
    private Long shopId;
    private String username;
    private String passwordHash;

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
