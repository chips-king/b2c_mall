package com.b2cmall.order.service;

import com.b2cmall.common.auth.AuthenticatedEmployee;
import com.b2cmall.common.exception.BusinessException;
import com.b2cmall.common.response.BaseResponseVO;
import com.b2cmall.order.context.RequestIdentityContext;
import com.b2cmall.order.dao.mapper.OrderMapper;
import com.b2cmall.order.dao.mapper.OrderSkuMapper;
import com.b2cmall.order.dao.po.OrderPO;
import com.b2cmall.order.dao.po.OrderSkuPO;
import com.b2cmall.order.feign.ProductFeignClient;
import com.b2cmall.order.feign.response.ProductVO;
import com.b2cmall.order.web.request.CreateOrderRequestVO;
import com.b2cmall.order.web.response.OrderResponseVO;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class OrderService {
    // 本阶段沿用课堂请求：每单一个SKU、一件商品，金额与商品整数价格使用相同单位。
    private static final int SINGLE_ITEM_QUANTITY = 1;
    private static final int PRODUCT_ON_SALE = 1;
    private final OrderStateService states;
    private final ProductFeignClient products;
    private final OrderMapper orders;
    private final OrderSkuMapper items;
    private final TransactionTemplate transaction;

    public OrderService(ProductFeignClient products, OrderMapper orders, OrderSkuMapper items,
                        PlatformTransactionManager manager, OrderStateService states) {
        this.states = states;
        this.products = products; this.orders = orders; this.items = items;
        this.transaction = new TransactionTemplate(manager);
    }

    public OrderResponseVO create(CreateOrderRequestVO request, String token) {
        AuthenticatedEmployee identity = RequestIdentityContext.require();
        if (request == null || request.getSkuId() == null || request.getSkuId() <= 0) {
            throw new BusinessException(400, "商品ID必须为正整数");
        }
        if (request.getSkuPrice() != null && request.getSkuPrice() <= 0) {
            throw new BusinessException(400, "商品价格必须为正整数");
        }
        // 先查询并校验远端商品，之后才开启本地事务，避免网络等待占用SQLite连接。
        ProductVO product = loadProduct(request.getSkuId(), token, identity.shopId());
        if (product.status() != PRODUCT_ON_SALE) { throw new BusinessException(400, "商品未上架"); }
        if (product.stock() < SINGLE_ITEM_QUANTITY) { throw new BusinessException(400, "商品库存不足"); }
        if (request.getSkuPrice() != null && !request.getSkuPrice().equals(product.price())) {
            throw new BusinessException(400, "商品价格不一致，请重新查询商品");
        }
        return transaction.execute(status -> {
            OrderPO order = new OrderPO();
            order.setShopId(identity.shopId()); order.setStatus(OrderPO.WAIT_PAY);
            order.setSkuTotal(SINGLE_ITEM_QUANTITY);
            order.setSkuTotalPrice(Math.multiplyExact(product.price(), SINGLE_ITEM_QUANTITY));
            order.setCreatedUserId(identity.userId()); order.setUpdateUserId(identity.userId());
            if (orders.insert(order) != 1 || order.getId() == null) { throw new IllegalStateException("订单保存失败"); }
            OrderSkuPO item = new OrderSkuPO();
            item.setOrderId(order.getId()); item.setShopId(identity.shopId()); item.setSkuId(product.id());
            item.setCateName(product.cateName()); item.setSkuName(product.skuName()); item.setSellPoint(product.sellPoint());
            item.setType(product.type()); item.setPrice(product.price()); item.setCreatedUserId(identity.userId());
            if (items.insert(item) != 1 || item.getId() == null) { throw new IllegalStateException("订单商品快照保存失败"); }
            // 订单与快照在同一事务中提交；查询返回数据库生成的真实ID和时间。
            return get(order.getId());
        });
    }

    @Transactional(readOnly = true)
    public OrderResponseVO get(long id) {
        long shopId = RequestIdentityContext.require().shopId();
        if (id <= 0) { throw new BusinessException(400, "订单ID必须为正整数"); }
        OrderPO order = orders.findById(id, shopId);
        if (order == null || order.getCreatedUserId() != RequestIdentityContext.require().userId()) { throw new BusinessException(404, "订单不存在"); }
        OrderSkuPO item = items.findByOrderId(id, shopId);
        if (item == null) { throw new IllegalStateException("订单缺少商品快照"); }
        return OrderResponseVO.from(order, item);
    }

    @Transactional
    public OrderResponseVO advance(long id, com.b2cmall.order.enums.OrderEvent event) {
        var identity = RequestIdentityContext.require();
        if (id <= 0) { throw new BusinessException(400, "订单ID必须为正整数"); }
        orders.lockForTransition(id, identity.shopId(), identity.userId());
        OrderPO order = orders.findById(id, identity.shopId());
        if (order == null || order.getCreatedUserId() != identity.userId()) { throw new BusinessException(404, "订单不存在"); }
        // 重复发货或完成只返回当前状态，不能倒退、不能重复记状态日志。
        boolean repeated = event == com.b2cmall.order.enums.OrderEvent.SENT
                ? OrderPO.SENT.equals(order.getStatus()) || OrderPO.COMPLETED.equals(order.getStatus())
                : OrderPO.COMPLETED.equals(order.getStatus());
        if (!repeated) { states.advance(order, event, identity.userId()); }
        return get(id);
    }

    private ProductVO loadProduct(long id, String token, long shopId) {
        BaseResponseVO<ProductVO> result;
        try { result = products.get(id, token); }
        catch (FeignException error) {
            if (error.status() == 401) { throw new BusinessException(401, "登录会话已失效"); }
            if (error.status() == 404) { throw new BusinessException(404, "商品不存在"); }
            // 不记录原始Feign异常，避免认证头或远端内部错误进入日志和响应。
            throw new BusinessException(503, "商品服务暂不可用");
        }
        if (result == null || result.getStatus() != 200 || result.getData() == null) { throw invalidProduct(); }
        ProductVO product = result.getData();
        if (product.id() == null || product.id() != id || product.shopId() == null || product.shopId() <= 0) {
            throw invalidProduct();
        }
        if (product.shopId() != shopId) { throw new BusinessException(404, "商品不存在"); }
        if (product.price() == null || product.price() <= 0 || product.stock() == null || product.stock() < 0
                || product.status() == null || (product.status() != 0 && product.status() != 1)
                || product.type() == null || (product.type() != 1 && product.type() != 2)
                || blank(product.skuName()) || blank(product.cateName()) || blank(product.sellPoint())) {
            throw invalidProduct();
        }
        return product;
    }
    private boolean blank(String value) { return value == null || value.isBlank(); }
    private BusinessException invalidProduct() { return new BusinessException(503, "商品服务响应异常"); }
}
