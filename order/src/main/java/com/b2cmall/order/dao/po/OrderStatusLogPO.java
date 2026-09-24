package com.b2cmall.order.dao.po;
/** 与状态变更同事务提交；每次真实推进只产生一条日志。 */
public record OrderStatusLogPO(long orderId, long shopId, long userId,
        String fromStatus, String toStatus, String event) { }
