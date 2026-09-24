package com.b2cmall.product.service;

import com.b2cmall.product.dao.mapper.SkuLogMapper;
import com.b2cmall.product.dao.po.SkuLogPO;
import com.b2cmall.product.dao.po.SkuPO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SkuLogService {
    private final SkuLogMapper logs;
    public SkuLogService(SkuLogMapper logs) { this.logs = logs; }
    /** 日志必须加入商品创建事务，不能在商品回滚时单独保留成功日志。 */
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(SkuPO sku) {
        SkuLogPO log = new SkuLogPO();
        log.setSkuId(sku.getId()); log.setShopId(sku.getShopId()); log.setPrice(sku.getPrice());
        log.setStock(sku.getStock()); log.setStatus(sku.getStatus()); log.setCreatedUserId(sku.getCreatedUserId());
        if (logs.insert(log) != 1 || log.getId() == null) { throw new IllegalStateException("商品日志保存失败"); }
    }
}
