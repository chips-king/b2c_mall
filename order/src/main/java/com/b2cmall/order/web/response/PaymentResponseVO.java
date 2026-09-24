package com.b2cmall.order.web.response;

import com.b2cmall.order.web.request.PayCallbackRequestVO;

/** callback是课堂模拟渠道生成的签名通知，提交前订单仍处于WAIT_PAY。 */
public record PaymentResponseVO(Long paymentId, Long orderId, String payType, Integer amount,
        String paymentStatus, String orderStatus, String channelTradeNo, String confirmedAt, PayCallbackRequestVO callback, PayCallbackRequestVO failureCallback) { }
