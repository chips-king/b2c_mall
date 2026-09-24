package com.b2cmall.order.dao.po;

/** 每次失败后重试创建新记录，保留历史；SUCCESS只能由校验通过的回调写入。 */
public class PaymentPO {
    public static final String PENDING = "PENDING";
    public static final String FAILED = "FAILED";
    public static final String SUCCESS = "SUCCESS";
    private Long id;
    private Long orderId;
    private Long shopId;
    private String payType;
    private Integer amount;
    private String status;
    private String channelTradeNo;
    private Long createdUserId;
    private Long updateUserId;
    private String confirmedAt;
    private String createdAt;
    private String updatedAt;
    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long value) { orderId = value; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long value) { shopId = value; }
    public String getPayType() { return payType; }
    public void setPayType(String value) { payType = value; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer value) { amount = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public String getChannelTradeNo() { return channelTradeNo; }
    public void setChannelTradeNo(String value) { channelTradeNo = value; }
    public Long getCreatedUserId() { return createdUserId; }
    public void setCreatedUserId(Long value) { createdUserId = value; }
    public Long getUpdateUserId() { return updateUserId; }
    public void setUpdateUserId(Long value) { updateUserId = value; }
    public String getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(String value) { confirmedAt = value; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String value) { createdAt = value; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String value) { updatedAt = value; }
}
