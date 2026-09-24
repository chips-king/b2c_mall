package com.b2cmall.order.context;

import com.b2cmall.common.auth.AuthenticatedEmployee;
import com.b2cmall.order.dao.po.*;
import com.b2cmall.order.enums.PayTypeEnum;
import com.b2cmall.order.web.request.PayRequestVO;
import com.b2cmall.order.web.response.PaymentResponseVO;

/** 每次支付独立创建；节点是单例，不能把订单、身份或token放在节点成员变量中。 */
public class PaymentContext {
    private final PayRequestVO request;
    private final String token;
    private final AuthenticatedEmployee identity;
    private PayTypeEnum payType;
    private OrderPO order;
    private OrderSkuPO snapshot;
    private String tradeNo;
    private PaymentResponseVO result;
    private boolean repeated;

    public PaymentContext(PayRequestVO request, String token, AuthenticatedEmployee identity) {
        this.request = request; this.token = token; this.identity = identity;
    }
    public PayRequestVO getRequest() { return request; }
    public String getToken() { return token; }
    public AuthenticatedEmployee getIdentity() { return identity; }
    public PayTypeEnum getPayType() { return payType; }
    public void setPayType(PayTypeEnum value) { payType = value; }
    public OrderPO getOrder() { return order; }
    public void setOrder(OrderPO value) { order = value; }
    public OrderSkuPO getSnapshot() { return snapshot; }
    public void setSnapshot(OrderSkuPO value) { snapshot = value; }
    public String getTradeNo() { return tradeNo; }
    public void setTradeNo(String value) { tradeNo = value; }
    public PaymentResponseVO getResult() { return result; }
    public void setResult(PaymentResponseVO value) { result = value; }
    public boolean isRepeated() { return repeated; }
    public void setRepeated(boolean value) { repeated = value; }
}
