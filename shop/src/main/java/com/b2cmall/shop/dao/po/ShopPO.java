package com.b2cmall.shop.dao.po;

/** 沿用老师的店铺字段；adminPassword 仅保存 BCrypt 哈希，不作为接口返回对象。 */
public class ShopPO {
    private Long id;
    private String shopName;
    private String adminAccount;
    private String adminPassword;
    private String logoUrl;
    private Integer status;
    private String initStatus;
    // SQLite 以 UTC ISO 8601 文本保存时间，避免依赖本机时区。
    private String createdAt;
    private String updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getAdminAccount() { return adminAccount; }
    public void setAdminAccount(String adminAccount) { this.adminAccount = adminAccount; }
    public String getAdminPassword() { return adminPassword; }
    public void setAdminPassword(String adminPassword) { this.adminPassword = adminPassword; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getInitStatus() { return initStatus; }
    public void setInitStatus(String initStatus) { this.initStatus = initStatus; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
