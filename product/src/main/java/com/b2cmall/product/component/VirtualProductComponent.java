package com.b2cmall.product.component;

import com.b2cmall.product.dao.mapper.SkuMapper;
import com.b2cmall.product.service.SkuLogService;
import com.b2cmall.product.web.request.CreateProductRequestVO;
import org.springframework.stereotype.Component;

@Component
public class VirtualProductComponent extends CreateProductTemplate {
    public VirtualProductComponent(SkuMapper skus, SkuLogService logs) { super(skus, logs); }
    @Override public int type() { return VIRTUAL; }
    @Override protected void checkPriceAndStock(CreateProductRequestVO request) {
        // 课堂Demo的虚拟商品同样记录有限库存，不自行引入无限库存或交付规则。
        requirePositive(request.getPrice(), "虚拟商品价格必须为正整数");
        requirePositive(request.getStock(), "虚拟商品库存必须为正整数");
    }
}
