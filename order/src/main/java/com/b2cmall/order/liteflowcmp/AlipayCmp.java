package com.b2cmall.order.liteflowcmp;

import com.b2cmall.order.context.PaymentContext;
import com.b2cmall.order.service.PaymentService;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

@LiteflowComponent("alipay")
public class AlipayCmp extends NodeComponent {
    private final PaymentService payments;
    public AlipayCmp(PaymentService payments) { this.payments = payments; }
    @Override public void process() {
        PaymentContext context = getContextBean(PaymentContext.class);
        payments.initiate(context, com.b2cmall.order.enums.PayTypeEnum.ALIPAY);
    }
}
