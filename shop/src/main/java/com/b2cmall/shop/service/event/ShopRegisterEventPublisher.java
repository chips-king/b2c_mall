package com.b2cmall.shop.service.event;

import com.google.common.eventbus.EventBus;
import com.b2cmall.shop.service.ShopInitializationStateService;
import com.b2cmall.shop.service.event.handler.InitEmployeeEventHandler;
import com.b2cmall.shop.service.event.handler.InitShopMessageEventHandler;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import org.springframework.stereotype.Component;

/** 只提交异步事件；成功、失败由对应事件回调持久化，HTTP 线程不等待员工调用。 */
@Component
public class ShopRegisterEventPublisher {
    private final EventBus eventBus;
    private final ShopInitializationStateService states;

    public ShopRegisterEventPublisher(EventBus eventBus, List<ShopRegisterObserver> observers,
                                      ShopInitializationStateService states) {
        // 异步模式不能靠 post 后检查结果来发现订阅者缺失，启动时严格检查两个业务步骤。
        if (observers.size() != 2
                || observers.stream().filter(InitEmployeeEventHandler.class::isInstance).count() != 1
                || observers.stream().filter(InitShopMessageEventHandler.class::isInstance).count() != 1) {
            throw new IllegalArgumentException("必须各配置一个管理员和欢迎消息订阅者");
        }
        this.eventBus = eventBus;
        this.states = states;
        observers.forEach(eventBus::register);
    }

    public CompletableFuture<Void> publish(Long shopId, long attempt) {
        ShopRegisterEvent event = new ShopRegisterEvent(shopId, attempt,
                () -> states.finish(shopId, attempt, true, null),
                failure -> states.finish(shopId, attempt, false,
                        failure instanceof RejectedExecutionException ? "QUEUE_FULL" : "STEP_FAILED"));
        event.begin();
        try { eventBus.post(event); }
        catch (RuntimeException exception) {
            event.fail(exception);
            throw exception;
        }
        return event.completion();
    }
}
