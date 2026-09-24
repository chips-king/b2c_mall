package com.b2cmall.order.liteflowcmp;

import com.b2cmall.order.context.PaymentContext;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeSwitchComponent;

/** 对外枚举与课堂节点ID显式映射，不允许请求任意指定流程节点。 */
@LiteflowComponent("payswitch")
public class PaySwitchCmp extends NodeSwitchComponent {
    @Override public String processSwitch() {
        PaymentContext context = getContextBean(PaymentContext.class);
        return switch (context.getPayType()) {
            case ALIPAY -> "alipay";
            case WECHAT -> "wechat";
        };
    }
}
