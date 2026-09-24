package com.b2cmall.product.service;

import com.b2cmall.common.exception.BusinessException;
import com.b2cmall.product.component.CreateProductTemplate;
import com.b2cmall.product.context.RequestIdentityContext;
import com.b2cmall.product.dao.mapper.SkuMapper;
import com.b2cmall.product.dao.po.SkuPO;
import com.b2cmall.product.web.request.CreateProductRequestVO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final Map<Integer, CreateProductTemplate> templates;
    private final SkuMapper skus;
    public ProductService(List<CreateProductTemplate> candidates, SkuMapper skus) {
        Map<Integer, CreateProductTemplate> collected = new HashMap<>();
        for (CreateProductTemplate template : candidates) {
            if (collected.putIfAbsent(template.type(), template) != null) {
                throw new IllegalStateException("商品类型的模板重复");
            }
        }
        if (!collected.keySet().equals(java.util.Set.of(CreateProductTemplate.PHYSICAL, CreateProductTemplate.VIRTUAL))) {
            throw new IllegalStateException("两种商品模板必须完整注册");
        }
        this.templates = Map.copyOf(collected); this.skus = skus;
    }
    /** 外层Spring事务包住整个模板，任意步骤异常都回滚商品、上架状态和日志。 */
    @Transactional
    public SkuPO create(CreateProductRequestVO request) {
        RequestIdentityContext.require();
        CreateProductTemplate template = request == null || request.getType() == null ? null : templates.get(request.getType());
        if (template == null) { throw new BusinessException(400, "商品类型必须为1或2"); }
        SkuPO created = template.createProduct(request);
        return get(created.getId());
    }
    public SkuPO get(long id) {
        if (id <= 0) { throw new BusinessException(400, "商品ID必须为正整数"); }
        long shopId = RequestIdentityContext.require().shopId();
        SkuPO sku = skus.findById(id, shopId);
        // 跨店铺查询与不存在使用同一结果，避免暴露其他店铺的商品信息。
        if (sku == null) { throw new BusinessException(404, "商品不存在"); }
        return sku;
    }
}
