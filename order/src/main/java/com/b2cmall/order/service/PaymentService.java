package com.b2cmall.order.service;

import com.b2cmall.common.auth.AuthenticatedEmployee;
import com.b2cmall.common.exception.BusinessException;
import com.b2cmall.order.context.RequestIdentityContext;
import com.b2cmall.order.context.PaymentContext;
import com.b2cmall.order.feign.ProductFeignClient;
import com.b2cmall.order.feign.response.ProductVO;
import com.b2cmall.common.response.BaseResponseVO;
import feign.FeignException;
import org.springframework.transaction.annotation.Propagation;
import com.b2cmall.order.dao.mapper.*;
import com.b2cmall.order.dao.po.*;
import com.b2cmall.order.enums.PayTypeEnum;
import com.b2cmall.order.web.request.*;
import com.b2cmall.order.web.response.PaymentResponseVO;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    private final OrderStateService states;
    private final ProductFeignClient products;
    private final OrderMapper orders;
    private final OrderSkuMapper items;
    private final PaymentMapper payments;
    private final PaySignatureService signatures;
    private final Map<PayTypeEnum, PayStrategyService> strategies;
    public PaymentService(OrderMapper orders, OrderSkuMapper items, PaymentMapper payments,
                          PaySignatureService signatures, List<PayStrategyService> candidates, ProductFeignClient products, OrderStateService states) {
        this.states = states;
        this.products = products;
        this.orders = orders; this.items = items; this.payments = payments; this.signatures = signatures;
        Map<PayTypeEnum, PayStrategyService> collected = new EnumMap<>(PayTypeEnum.class);
        for (PayStrategyService strategy : candidates) {
            if (collected.putIfAbsent(strategy.payType(), strategy) != null) { throw new IllegalStateException("支付策略重复"); }
        }
        if (collected.size() != PayTypeEnum.values().length) { throw new IllegalStateException("支付策略必须完整注册"); }
        strategies = Map.copyOf(collected);
    }

    /** 价格节点先领取订单，再校验成交快照；重复支付只读回原记录。 */
    @Transactional(propagation = Propagation.MANDATORY)
    public void checkPrice(PaymentContext context) {
        AuthenticatedEmployee identity = context.getIdentity();
        PayRequestVO request = context.getRequest();
        if (request == null || request.getOrderId() == null || request.getOrderId() <= 0) {
            throw new BusinessException(400, "订单ID必须为正整数");
        }
        PayTypeEnum type = PayTypeEnum.parse(request.getPayType());
        context.setPayType(type);
        // 首条SQL取得写锁，串行处理同一订单的支付尝试；发起支付不改变WAIT_PAY。
        orders.lockForPayment(request.getOrderId(), identity.shopId(), identity.userId());
        OrderPO order = requireOrder(request.getOrderId(), identity.shopId(), identity.userId());
        verifySnapshot(order);
        context.setOrder(order);
        context.setSnapshot(items.findByOrderId(order.getId(), identity.shopId()));
        PaymentPO existing = payments.findByOrderId(order.getId(), identity.shopId());
        if (existing != null && !PaymentPO.FAILED.equals(existing.getStatus())) {
            if (!existing.getPayType().equals(type.name())) { throw new BusinessException(409, "订单已使用其他支付渠道"); }
            verifyAmount(order, existing);
            context.setRepeated(true);
            context.setResult(response(order, existing));
        } else if (!OrderPO.WAIT_PAY.equals(order.getStatus())) {
            throw new BusinessException(409, "订单当前状态不能发起支付");
        }
    }

    public void checkStock(PaymentContext context) {
        // 已受理的支付不重新查询商品，避免商品变化或远程故障破坏重复请求语义。
        if (context.isRepeated()) { return; }
        long skuId = context.getSnapshot().getSkuId();
        BaseResponseVO<ProductVO> response;
        try { response = products.get(skuId, context.getToken()); }
        catch (FeignException error) {
            if (error.status() == 401) { throw new BusinessException(401, "登录会话已失效"); }
            if (error.status() == 404) { throw new BusinessException(404, "商品不存在"); }
            throw new BusinessException(503, "商品服务暂不可用");
        }
        if (response == null || response.getStatus() != 200 || response.getData() == null) {
            throw new BusinessException(503, "商品服务响应异常");
        }
        ProductVO product = response.getData();
        if (product.id() == null || product.id() != skuId || product.shopId() == null || product.shopId() <= 0
                || product.stock() == null || product.stock() < 0 || product.status() == null
                || (product.status() != 0 && product.status() != 1)) {
            throw new BusinessException(503, "商品服务响应异常");
        }
        if (product.shopId() != context.getIdentity().shopId()) { throw new BusinessException(404, "商品不存在"); }
        if (product.status() != 1) { throw new BusinessException(400, "商品未上架"); }
        if (product.stock() < context.getOrder().getSkuTotal()) { throw new BusinessException(400, "商品库存不足"); }
        // 本课堂阶段只校验库存；支付金额仍来自已校验的订单成交快照。
    }

    public void initiate(PaymentContext context, PayTypeEnum channel) {
        if (context.getPayType() != channel) { throw new IllegalStateException("支付节点与渠道不一致"); }
        if (context.isRepeated()) { return; }
        String tradeNo = strategies.get(channel).initiate(context.getOrder().getId(), context.getOrder().getSkuTotalPrice());
        if (tradeNo == null || !tradeNo.matches("[A-Za-z0-9-]+")) { throw new IllegalStateException("模拟渠道未返回有效流水"); }
        context.setTradeNo(tradeNo);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void recordPayment(PaymentContext context) {
        if (context.isRepeated()) { return; }
        if (context.getTradeNo() == null) { throw new IllegalStateException("支付渠道尚未受理"); }
        OrderPO order = context.getOrder();
        AuthenticatedEmployee identity = context.getIdentity();
        PaymentPO payment = new PaymentPO();
        payment.setOrderId(order.getId()); payment.setShopId(identity.shopId()); payment.setPayType(context.getPayType().name());
        payment.setAmount(order.getSkuTotalPrice()); payment.setStatus(PaymentPO.PENDING); payment.setChannelTradeNo(context.getTradeNo());
        payment.setCreatedUserId(identity.userId()); payment.setUpdateUserId(identity.userId());
        if (payments.insert(payment) != 1 || payment.getId() == null) { throw new IllegalStateException("支付记录保存失败"); }
        // 沿用支付表记录本次发起结果，只保存PENDING；PAID仍只允许由合法回调确认。
        context.setResult(response(order, payments.findById(payment.getId(), identity.shopId())));
    }

    @Transactional
    public PaymentResponseVO callback(PayCallbackRequestVO request) {
        AuthenticatedEmployee identity = RequestIdentityContext.require();
        validateCallback(request);
        if (!signatures.verify(request)) { throw new BusinessException(400, "支付回调签名无效"); }
        // 更新条件绑定订单、店铺、渠道、流水和金额；重复成功通知不会再次更新确认时间。
        int confirmed = payments.confirm(request, identity.shopId(), identity.userId());
        OrderPO order = requireOrder(request.getOrderId(), identity.shopId(), identity.userId());
        PaymentPO payment = payments.findById(request.getPaymentId(), identity.shopId());
        if (payment == null) { throw new BusinessException(404, "支付记录不存在"); }
        verifySnapshot(order); verifyAmount(order, payment);
        if (!payment.getOrderId().equals(request.getOrderId()) || !payment.getPayType().equals(request.getPayType())
                || !payment.getAmount().equals(request.getAmount()) || !payment.getChannelTradeNo().equals(request.getChannelTradeNo())) {
            throw new BusinessException(400, "支付回调与支付记录不一致");
        }
        if (confirmed == 1) {
            // 成功回调才驱动PAY事件；失败记录保留，订单继续待支付，允许新尝试。
            if (PaymentPO.SUCCESS.equals(request.getResult())) {
                states.advance(order, com.b2cmall.order.enums.OrderEvent.PAY, identity.userId());
            }
        } else if (!request.getResult().equals(payment.getStatus())) {
            // 已失败的旧尝试不能被迟到成功通知覆盖，也不能影响后续支付。
            throw new BusinessException(409, "支付尝试已结束或订单状态不允许确认");
        }
        return response(order, payment);
    }

    private void validateCallback(PayCallbackRequestVO request) {
        if (request == null || request.getOrderId() == null || request.getOrderId() <= 0
                || request.getPaymentId() == null || request.getPaymentId() <= 0
                || request.getAmount() == null || request.getAmount() <= 0
                || request.getChannelTradeNo() == null || !request.getChannelTradeNo().matches("[A-Za-z0-9-]+")
                || !(PaymentPO.SUCCESS.equals(request.getResult()) || PaymentPO.FAILED.equals(request.getResult()))) {
            throw new BusinessException(400, "支付回调参数无效");
        }
        PayTypeEnum.parse(request.getPayType());
    }
    private OrderPO requireOrder(long id, long shopId, long userId) {
        OrderPO order = orders.findById(id, shopId);
        if (order == null || order.getCreatedUserId() != userId) { throw new BusinessException(404, "订单不存在"); }
        return order;
    }
    private void verifySnapshot(OrderPO order) {
        OrderSkuPO snapshot = items.findByOrderId(order.getId(), order.getShopId());
        if (snapshot == null || !Integer.valueOf(1).equals(order.getSkuTotal())
                || !order.getSkuTotalPrice().equals(snapshot.getPrice())) {
            throw new IllegalStateException("订单金额与成交快照不一致");
        }
    }
    private void verifyAmount(OrderPO order, PaymentPO payment) {
        if (!order.getSkuTotalPrice().equals(payment.getAmount())) { throw new BusinessException(400, "支付金额与订单快照不一致"); }
    }
    private PaymentResponseVO response(OrderPO order, PaymentPO payment) {
        boolean paid = java.util.Set.of(OrderPO.PAID, OrderPO.SENT, OrderPO.COMPLETED).contains(order.getStatus());
        if (payment == null || !(PaymentPO.FAILED.equals(payment.getStatus())
                || OrderPO.WAIT_PAY.equals(order.getStatus()) && PaymentPO.PENDING.equals(payment.getStatus())
                || paid && PaymentPO.SUCCESS.equals(payment.getStatus()))) {
            throw new IllegalStateException("订单与支付记录状态不一致");
        }
        PayCallbackRequestVO callback = signedCallback(payment,
                PaymentPO.FAILED.equals(payment.getStatus()) ? PaymentPO.FAILED : PaymentPO.SUCCESS);
        PayCallbackRequestVO failureCallback = PaymentPO.PENDING.equals(payment.getStatus())
                ? signedCallback(payment, PaymentPO.FAILED) : null;
        return new PaymentResponseVO(payment.getId(), payment.getOrderId(), payment.getPayType(), payment.getAmount(),
                payment.getStatus(), order.getStatus(), payment.getChannelTradeNo(), payment.getConfirmedAt(), callback, failureCallback);
    }

    private PayCallbackRequestVO signedCallback(PaymentPO payment, String result) {
        // 仅供课堂模拟渠道返回通知；签名密钥留在服务端，成功、失败结果各自签名。
        PayCallbackRequestVO callback = new PayCallbackRequestVO();
        callback.setPaymentId(payment.getId()); callback.setOrderId(payment.getOrderId()); callback.setPayType(payment.getPayType());
        callback.setAmount(payment.getAmount()); callback.setChannelTradeNo(payment.getChannelTradeNo()); callback.setResult(result);
        callback.setSignature(signatures.sign(callback));
        return callback;
    }
}
