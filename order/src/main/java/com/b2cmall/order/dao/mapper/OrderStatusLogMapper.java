package com.b2cmall.order.dao.mapper;
import com.b2cmall.order.dao.po.OrderStatusLogPO;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface OrderStatusLogMapper { int insert(OrderStatusLogPO log); }
