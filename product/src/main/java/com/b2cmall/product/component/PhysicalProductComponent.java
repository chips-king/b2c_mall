package com.b2cmall.product.component;

import com.b2cmall.product.dao.mapper.SkuMapper;
import com.b2cmall.product.service.SkuLogService;
import com.b2cmall.product.web.request.CreateProductRequestVO;
import org.springframework.stereotype.Component;

@Component
public class PhysicalProductComponent extends CreateProductTemplate {
    public PhysicalProductComponent(SkuMapper skus, SkuLogService logs) { super(skus, logs); }
    @Override public int type() { return PHYSICAL; }
    @Override protected void checkPriceAndStock(CreateProductRequestVO request) {
        requirePositive(request.getPrice(), "实物商品价格必须为正整数");
        requirePositive(request.getStock(), "实物商品库存必须为正整数");
    }
}
