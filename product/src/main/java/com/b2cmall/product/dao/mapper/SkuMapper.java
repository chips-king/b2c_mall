package com.b2cmall.product.dao.mapper;

import com.b2cmall.product.dao.po.SkuPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SkuMapper {
    int insert(SkuPO sku);
    int putOnSale(@Param("id") long id, @Param("shopId") long shopId, @Param("userId") long userId);
    SkuPO findById(@Param("id") long id, @Param("shopId") long shopId);
}
