package com.b2cmall.order.dao.po;

/** 订单归属与操作人来自已验证身份，商品信息按下单时快照保存。 */
public class OrderSkuPO {
    private Long id;
    private Long orderId;
    private Long shopId;
    private Long skuId;
    private String cateName;
    private String skuName;
    private String sellPoint;
    private Integer type;
    private Integer price;
    private Long createdUserId;
    private String createdAt;
    private String updatedAt;
    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long value) { orderId = value; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long value) { shopId = value; }
    public Long getSkuId() { return skuId; }
    public void setSkuId(Long value) { skuId = value; }
    public String getCateName() { return cateName; }
    public void setCateName(String value) { cateName = value; }
    public String getSkuName() { return skuName; }
    public void setSkuName(String value) { skuName = value; }
    public String getSellPoint() { return sellPoint; }
    public void setSellPoint(String value) { sellPoint = value; }
    public Integer getType() { return type; }
    public void setType(Integer value) { type = value; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer value) { price = value; }
    public Long getCreatedUserId() { return createdUserId; }
    public void setCreatedUserId(Long value) { createdUserId = value; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String value) { createdAt = value; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String value) { updatedAt = value; }
}
