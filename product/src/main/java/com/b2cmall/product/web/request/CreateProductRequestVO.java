package com.b2cmall.product.web.request;

/** 沿用课堂商品字段；店铺和创建人不属于客户端可赋值字段。 */
public class CreateProductRequestVO {
    private String cateName;
    private String skuName;
    private String sellPoint;
    private Integer stock;
    private Integer price;
    private Integer type;
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
    public Integer getType() { return type; }
    public void setType(Integer value) { type = value; }
}
