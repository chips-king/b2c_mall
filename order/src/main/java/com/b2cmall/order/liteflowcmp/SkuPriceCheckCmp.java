package com.b2cmall.order.liteflowcmp;

import com.b2cmall.order.context.PaymentContext;
import com.b2cmall.order.service.PaymentService;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

@LiteflowComponent("priceCheck")
public class SkuPriceCheckCmp extends NodeComponent {
    private final PaymentService payments;
    public SkuPriceCheckCmp(PaymentService payments) { this.payments = payments; }
    @Override public void process() {
        PaymentContext context = getContextBean(PaymentContext.class);
        payments.checkPrice(context);
    }
}
