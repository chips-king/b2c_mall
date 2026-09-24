package com.b2cmall.product.dao.po;

/** 商品持久化数据，shopId用于店铺隔离，操作人来自请求身份。 */
public class SkuLogPO {
    private Long id;
    private Long skuId;
    private Long shopId;
    private Integer stock;
    private Integer price;
    private Integer status;
    private Long createdUserId;
    private String createdAt;
    private String updatedAt;
    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Long getSkuId() { return skuId; }
    public void setSkuId(Long value) { skuId = value; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long value) { shopId = value; }
    public Integer getStock() { return stock; }
    public void setStock(Integer value) { stock = value; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer value) { price = value; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer value) { status = value; }
    public Long getCreatedUserId() { return createdUserId; }
    public void setCreatedUserId(Long value) { createdUserId = value; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String value) { createdAt = value; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String value) { updatedAt = value; }
}
