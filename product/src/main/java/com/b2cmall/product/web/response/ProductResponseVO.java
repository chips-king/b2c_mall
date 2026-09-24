package com.b2cmall.product.web.response;

import com.b2cmall.product.dao.po.SkuPO;

public record ProductResponseVO(Long id, Long shopId, String cateName, String skuName, String sellPoint,
        Integer stock, Integer price, Integer status, Integer type, Long createdUserId, Long updateUserId,
        String createdAt, String updatedAt) {
    public static ProductResponseVO from(SkuPO sku) {
        return new ProductResponseVO(sku.getId(), sku.getShopId(), sku.getCateName(), sku.getSkuName(), sku.getSellPoint(),
                sku.getStock(), sku.getPrice(), sku.getStatus(), sku.getType(), sku.getCreatedUserId(), sku.getUpdateUserId(),
                sku.getCreatedAt(), sku.getUpdatedAt());
    }
}
