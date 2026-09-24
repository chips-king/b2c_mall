package com.b2cmall.product.dao.mapper;

import com.b2cmall.product.dao.po.SkuLogPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkuLogMapper { int insert(SkuLogPO log); }
