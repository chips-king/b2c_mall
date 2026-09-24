package com.b2cmall.order.service;

import com.b2cmall.order.config.OrderStateMachineConfig;
import com.b2cmall.order.dao.mapper.*;
import com.b2cmall.order.dao.po.*;
import com.b2cmall.order.enums.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

@Service
public class OrderStateService {
    private final OrderStateMachineConfig machines;
    private final OrderMapper orders;
    private final OrderStatusLogMapper logs;
    public OrderStateService(OrderStateMachineConfig machines, OrderMapper orders, OrderStatusLogMapper logs) {
        this.machines = machines; this.orders = orders; this.logs = logs;
    }
    @Transactional(propagation = Propagation.MANDATORY)
    public void advance(OrderPO order, OrderEvent event, long userId) {
        String before = order.getStatus();
        String after = machines.next(OrderStatus.valueOf(before), event).name();
        if (orders.transition(order.getId(), order.getShopId(), userId, before, after) != 1) {
            throw new IllegalStateException("订单状态并发变更，事务回滚");
        }
        if (logs.insert(new OrderStatusLogPO(order.getId(), order.getShopId(), userId, before, after, event.name())) != 1) {
            throw new IllegalStateException("状态日志保存失败，事务回滚");
        }
        order.setStatus(after);
    }
}
