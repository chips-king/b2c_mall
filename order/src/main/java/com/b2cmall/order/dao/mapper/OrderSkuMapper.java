package com.b2cmall.order.dao.mapper;

import com.b2cmall.order.dao.po.OrderSkuPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderSkuMapper {
    int insert(OrderSkuPO item);
    OrderSkuPO findByOrderId(@Param("orderId") long orderId, @Param("shopId") long shopId);
}
