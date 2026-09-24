package com.b2cmall.shop.dao.po;

/** 店铺初始化的持久化执行编号；旧执行只能完成自己的编号，不能覆盖新一轮重试。 */
public class ShopInitializationTaskPO {
    private Long shopId;
    private Long attempt;
    private Long deadlineEpochMs;
    private String initStatus;
    private String failureCode;
    private boolean employeeInitialized;
    public boolean isEmployeeInitialized() { return employeeInitialized; }
    public void setEmployeeInitialized(boolean value) { employeeInitialized = value; }

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public Long getAttempt() { return attempt; }
    public void setAttempt(Long attempt) { this.attempt = attempt; }
    public Long getDeadlineEpochMs() { return deadlineEpochMs; }
    public void setDeadlineEpochMs(Long deadlineEpochMs) { this.deadlineEpochMs = deadlineEpochMs; }
    public String getInitStatus() { return initStatus; }
    public void setInitStatus(String initStatus) { this.initStatus = initStatus; }
    public String getFailureCode() { return failureCode; }
    public void setFailureCode(String failureCode) { this.failureCode = failureCode; }
}
