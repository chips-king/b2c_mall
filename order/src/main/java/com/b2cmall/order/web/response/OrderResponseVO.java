package com.b2cmall.order.web.response;

import com.b2cmall.order.dao.po.OrderPO;
import com.b2cmall.order.dao.po.OrderSkuPO;

/** 查询返回下单时保存的商品与价格，商品后续修改不改变订单成交快照。 */
public record OrderResponseVO(Long id, Long shopId, String status, Integer skuTotal, Integer skuTotalPrice,
        Long createdUserId, Long updateUserId, String createdAt, String updatedAt, Item sku) {
    public record Item(Long skuId, String cateName, String skuName, String sellPoint, Integer type, Integer price) { }
    public static OrderResponseVO from(OrderPO order, OrderSkuPO item) {
        return new OrderResponseVO(order.getId(), order.getShopId(), order.getStatus(), order.getSkuTotal(),
                order.getSkuTotalPrice(), order.getCreatedUserId(), order.getUpdateUserId(), order.getCreatedAt(),
                order.getUpdatedAt(), new Item(item.getSkuId(), item.getCateName(), item.getSkuName(),
                item.getSellPoint(), item.getType(), item.getPrice()));
    }
}
