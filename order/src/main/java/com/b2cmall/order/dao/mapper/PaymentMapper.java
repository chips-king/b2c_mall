package com.b2cmall.order.dao.mapper;

import com.b2cmall.order.dao.po.PaymentPO;
import com.b2cmall.order.web.request.PayCallbackRequestVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {
    int insert(PaymentPO payment);
    PaymentPO findById(@Param("id") long id, @Param("shopId") long shopId);
    PaymentPO findByOrderId(@Param("orderId") long orderId, @Param("shopId") long shopId);
    int confirm(@Param("callback") PayCallbackRequestVO callback, @Param("shopId") long shopId, @Param("userId") long userId);
}
