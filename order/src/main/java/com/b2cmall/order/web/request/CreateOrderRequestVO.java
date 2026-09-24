package com.b2cmall.order.web.request;

/** 沿用课堂skuId、skuPrice；skuPrice仅用于比对，实际成交金额来自Product。 */
public class CreateOrderRequestVO {
    private Long skuId;
    private Integer skuPrice;
    public Long getSkuId() { return skuId; }
    public void setSkuId(Long value) { skuId = value; }
    public Integer getSkuPrice() { return skuPrice; }
    public void setSkuPrice(Integer value) { skuPrice = value; }
}
