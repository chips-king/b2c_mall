package com.b2cmall.order.web.request;

public class PayRequestVO {
    private Long orderId;
    private String payType;
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long value) { orderId = value; }
    public String getPayType() { return payType; }
    public void setPayType(String value) { payType = value; }
}
