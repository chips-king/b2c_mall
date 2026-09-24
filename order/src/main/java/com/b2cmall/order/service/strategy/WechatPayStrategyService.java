package com.b2cmall.order.service.strategy;

import com.b2cmall.order.enums.PayTypeEnum;
import com.b2cmall.order.service.PayStrategyService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class WechatPayStrategyService implements PayStrategyService {
    @Override public PayTypeEnum payType() { return PayTypeEnum.WECHAT; }
    @Override public String initiate(long orderId, int amount) {
        if (orderId <= 0 || amount <= 0) { throw new IllegalArgumentException("模拟支付参数无效"); }
        return "WECHAT-MOCK-" + UUID.randomUUID();
    }
}
