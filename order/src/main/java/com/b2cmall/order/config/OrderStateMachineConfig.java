package com.b2cmall.order.config;

import com.b2cmall.common.exception.BusinessException;
import com.b2cmall.order.enums.*;
import java.util.EnumSet;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** 每次以数据库状态新建状态机；不缓存订单实例，避免并发串单和回滚后内存状态残留。 */
@Component
public class OrderStateMachineConfig {
    public OrderStatus next(OrderStatus current, OrderEvent event) {
        StateMachine<OrderStatus, OrderEvent> machine;
        try {
            var builder = StateMachineBuilder.<OrderStatus, OrderEvent>builder();
            builder.configureStates().withStates().initial(current).states(EnumSet.allOf(OrderStatus.class));
            builder.configureTransitions()
                    .withExternal().source(OrderStatus.WAIT_PAY).target(OrderStatus.PAID).event(OrderEvent.PAY)
                    .and().withExternal().source(OrderStatus.PAID).target(OrderStatus.SENT).event(OrderEvent.SENT)
                    .and().withExternal().source(OrderStatus.SENT).target(OrderStatus.COMPLETED).event(OrderEvent.COMPLETED);
            machine = builder.build();
        } catch (Exception error) { throw new IllegalStateException("订单状态机配置失败", error); }
        try {
            machine.startReactively().block();
            var results = machine.sendEvent(Mono.just(MessageBuilder.withPayload(event).build())).collectList().block();
            if (results == null || results.size() != 1
                    || results.get(0).getResultType() != StateMachineEventResult.ResultType.ACCEPTED) {
                throw new BusinessException(409, "当前订单状态不允许此操作");
            }
            results.get(0).complete().block();
            if (machine.hasStateMachineError()) { throw new IllegalStateException("订单状态转换失败"); }
            return machine.getState().getId();
        } finally { machine.stopReactively().block(); }
    }
}
