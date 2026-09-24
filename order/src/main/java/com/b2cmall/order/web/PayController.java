package com.b2cmall.order.web;

import com.b2cmall.common.response.BaseResponseVO;
import com.b2cmall.order.service.PaymentService;
import com.b2cmall.order.context.PaymentContext;
import com.b2cmall.order.context.RequestIdentityContext;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import org.springframework.transaction.annotation.Transactional;
import com.b2cmall.order.web.request.*;
import com.b2cmall.order.web.response.PaymentResponseVO;
import org.springframework.web.bind.annotation.*;

/** 两个接口均沿用员工鉴权；回调额外校验模拟渠道签名，不把浏览器的支付成功声明当作付款依据。 */
@RestController
@RequestMapping("/order")
public class PayController {
    private final PaymentService payments;
    private final FlowExecutor flows;
    public PayController(PaymentService payments, FlowExecutor flows) { this.payments = payments; this.flows = flows; }
    @PostMapping("/pay")
    @Transactional
    public BaseResponseVO<PaymentResponseVO> pay(@RequestBody PayRequestVO request,
            @RequestHeader("Authorization") String token) {
        PaymentContext context = new PaymentContext(request, token, RequestIdentityContext.require());
        LiteflowResponse result = flows.execute2Resp("payChain", null, context);
        // execute2Resp把节点异常放进结果；必须在事务提交前重新抛出，否则流程失败也可能提交已写入的支付记录。
        if (!result.isSuccess()) {
            if (result.getCause() instanceof RuntimeException cause) { throw cause; }
            throw new IllegalStateException("支付流程执行失败", result.getCause());
        }
        if (context.getResult() == null) { throw new IllegalStateException("支付流程缺少支付记录"); }
        return BaseResponseVO.success(context.getResult());
    }
    @PostMapping("/callback")
    public BaseResponseVO<PaymentResponseVO> callback(@RequestBody PayCallbackRequestVO request) {
        return BaseResponseVO.success(payments.callback(request));
    }
}
