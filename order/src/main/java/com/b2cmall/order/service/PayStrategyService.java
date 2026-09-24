package com.b2cmall.order.service;

import com.b2cmall.order.enums.PayTypeEnum;

/** 模拟策略只创建渠道流水；订单是否已支付由后续签名回调确认。 */
public interface PayStrategyService {
    PayTypeEnum payType();
    String initiate(long orderId, int amount);
}
