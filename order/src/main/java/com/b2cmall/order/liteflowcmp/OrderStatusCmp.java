package com.b2cmall.order.liteflowcmp;

import com.b2cmall.order.context.PaymentContext;
import com.b2cmall.order.service.PaymentService;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

@LiteflowComponent("orderStatus")
public class OrderStatusCmp extends NodeComponent {
    private final PaymentService payments;
    public OrderStatusCmp(PaymentService payments) { this.payments = payments; }
    @Override public void process() {
        PaymentContext context = getContextBean(PaymentContext.class);
        payments.recordPayment(context);
    }
}
