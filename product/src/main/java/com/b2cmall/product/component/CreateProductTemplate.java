package com.b2cmall.product.component;

import com.b2cmall.common.auth.AuthenticatedEmployee;
import com.b2cmall.common.exception.BusinessException;
import com.b2cmall.product.context.RequestIdentityContext;
import com.b2cmall.product.dao.mapper.SkuMapper;
import com.b2cmall.product.dao.po.SkuPO;
import com.b2cmall.product.service.SkuLogService;
import com.b2cmall.product.web.request.CreateProductRequestVO;

/** 模板固定创建顺序，子类只实现类型及价格库存检查；事务由外层ProductService统一管理。 */
public abstract class CreateProductTemplate {
    public static final int PHYSICAL = 1;
    public static final int VIRTUAL = 2;
    public static final int DRAFT = 0;
    public static final int ON_SALE = 1;
    private final SkuMapper skus;
    private final SkuLogService logs;
    protected CreateProductTemplate(SkuMapper skus, SkuLogService logs) { this.skus = skus; this.logs = logs; }
    public abstract int type();
    protected abstract void checkPriceAndStock(CreateProductRequestVO request);

    public final SkuPO createProduct(CreateProductRequestVO request) {
        checkParameters(request);
        checkCategory(request);
        checkPriceAndStock(request);
        checkContent(request);
        SkuPO sku = saveProduct(request);
        putOnSale(sku);
        afterCreation(sku);
        return sku;
    }
    protected void checkParameters(CreateProductRequestVO request) {
        if (request == null || request.getType() == null || request.getType() != type()) {
            throw new BusinessException(400, "商品类型必须为1或2，并匹配创建模板");
        }
    }
    protected void checkCategory(CreateProductRequestVO request) {
        requireText(request.getCateName(), "类目不能为空");
    }
    protected void checkContent(CreateProductRequestVO request) {
        requireText(request.getSkuName(), "商品名称不能为空");
        requireText(request.getSellPoint(), "商品卖点不能为空");
    }
    protected SkuPO saveProduct(CreateProductRequestVO request) {
        AuthenticatedEmployee identity = RequestIdentityContext.require();
        SkuPO sku = new SkuPO();
        sku.setShopId(identity.shopId());
        sku.setCreatedUserId(identity.userId()); sku.setUpdateUserId(identity.userId());
        sku.setCateName(request.getCateName().strip()); sku.setSkuName(request.getSkuName().strip());
        sku.setSellPoint(request.getSellPoint().strip()); sku.setPrice(request.getPrice()); sku.setStock(request.getStock());
        sku.setType(type()); sku.setStatus(DRAFT);
        if (skus.insert(sku) != 1 || sku.getId() == null) { throw new IllegalStateException("商品保存失败"); }
        return sku;
    }
    protected void putOnSale(SkuPO sku) {
        // 上架只允许从本店草稿状态转换；失败会连同刚保存的商品一起回滚。
        if (skus.putOnSale(sku.getId(), sku.getShopId(), sku.getUpdateUserId()) != 1) {
            throw new IllegalStateException("商品上架失败");
        }
        sku.setStatus(ON_SALE);
    }
    protected void afterCreation(SkuPO sku) { logs.save(sku); }
    protected void requirePositive(Integer value, String message) {
        if (value == null || value <= 0) { throw new BusinessException(400, message); }
    }
    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) { throw new BusinessException(400, message); }
    }
}
