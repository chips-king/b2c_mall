package com.b2cmall.order.dao.po;

/** 订单归属与操作人来自已验证身份，商品信息按下单时快照保存。 */
public class OrderPO {
    public static final String WAIT_PAY = "WAIT_PAY";
    public static final String PAID = "PAID";
    public static final String SENT = "SENT";
    public static final String COMPLETED = "COMPLETED";
    private Long id;
    private Long shopId;
    private String status;
    private Integer skuTotal;
    private Integer skuTotalPrice;
    private Long createdUserId;
    private Long updateUserId;
    private String createdAt;
    private String updatedAt;
    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long value) { shopId = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public Integer getSkuTotal() { return skuTotal; }
    public void setSkuTotal(Integer value) { skuTotal = value; }
    public Integer getSkuTotalPrice() { return skuTotalPrice; }
    public void setSkuTotalPrice(Integer value) { skuTotalPrice = value; }
    public Long getCreatedUserId() { return createdUserId; }
    public void setCreatedUserId(Long value) { createdUserId = value; }
    public Long getUpdateUserId() { return updateUserId; }
    public void setUpdateUserId(Long value) { updateUserId = value; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String value) { createdAt = value; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String value) { updatedAt = value; }
}
