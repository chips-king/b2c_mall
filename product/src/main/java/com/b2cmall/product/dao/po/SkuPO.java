package com.b2cmall.product.dao.po;

/** 商品持久化数据，shopId用于店铺隔离，操作人来自请求身份。 */
public class SkuPO {
    private Long id;
    private Long shopId;
    private String cateName;
    private String skuName;
    private String sellPoint;
    private Integer stock;
    private Integer price;
    private Integer status;
    private Integer type;
    private Long createdUserId;
    private Long updateUserId;
    private String createdAt;
    private String updatedAt;
    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long value) { shopId = value; }
    public String getCateName() { return cateName; }
    public void setCateName(String value) { cateName = value; }
    public String getSkuName() { return skuName; }
    public void setSkuName(String value) { skuName = value; }
    public String getSellPoint() { return sellPoint; }
    public void setSellPoint(String value) { sellPoint = value; }
    public Integer getStock() { return stock; }
    public void setStock(Integer value) { stock = value; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer value) { price = value; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer value) { status = value; }
    public Integer getType() { return type; }
    public void setType(Integer value) { type = value; }
    public Long getCreatedUserId() { return createdUserId; }
    public void setCreatedUserId(Long value) { createdUserId = value; }
    public Long getUpdateUserId() { return updateUserId; }
    public void setUpdateUserId(Long value) { updateUserId = value; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String value) { createdAt = value; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String value) { updatedAt = value; }
}
