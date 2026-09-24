package com.b2cmall.order.dao.mapper;

import com.b2cmall.order.dao.po.OrderPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper {
    int insert(OrderPO order);
    OrderPO findById(@Param("id") long id, @Param("shopId") long shopId);
    int lockForPayment(@Param("id") long id, @Param("shopId") long shopId, @Param("userId") long userId);
    int lockForTransition(@Param("id") long id, @Param("shopId") long shopId, @Param("userId") long userId);
    int transition(@Param("id") long id, @Param("shopId") long shopId, @Param("userId") long userId,
                   @Param("before") String before, @Param("after") String after);
}
