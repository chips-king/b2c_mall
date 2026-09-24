package com.b2cmall.order.web.request;

public class PayCallbackRequestVO {
    private Long paymentId;
    private Long orderId;
    private String payType;
    private Integer amount;
    private String channelTradeNo;
    private String result;
    private String signature;
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long value) { paymentId = value; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long value) { orderId = value; }
    public String getPayType() { return payType; }
    public void setPayType(String value) { payType = value; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer value) { amount = value; }
    public String getChannelTradeNo() { return channelTradeNo; }
    public void setChannelTradeNo(String value) { channelTradeNo = value; }
    public String getResult() { return result; }
    public void setResult(String value) { result = value; }
    public String getSignature() { return signature; }
    public void setSignature(String value) { signature = value; }
}
