package com.b2cmall.shop.config;

import com.b2cmall.shop.service.event.ShopRegisterEvent;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableScheduling
public class ShopInitializationConfig {
    private static final Logger log = LoggerFactory.getLogger(ShopInitializationConfig.class);

    @Bean
    public ThreadPoolTaskExecutor shopInitializationExecutor(
            @Value("${mall.initialization.threads:2}") int threads,
            @Value("${mall.initialization.queue-capacity:100}") int capacity,
            @Value("${mall.initialization.timeout-seconds:60}") int timeout,
            @Value("${mall.initialization.scan-interval-ms:5000}") long scanInterval) {
        if (threads <= 0 || capacity <= 0 || timeout <= 0 || scanInterval <= 0) {
            throw new IllegalArgumentException("初始化线程数、队列容量、超时和扫描间隔必须大于零");
        }
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threads);
        executor.setMaxPoolSize(threads);
        executor.setQueueCapacity(capacity);
        executor.setThreadNamePrefix("shop-initialization-");
        // 队列满必须拒绝并记录失败，不能在 HTTP 线程悄悄同步执行或丢弃任务。
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(timeout);
        return executor;
    }

    @Bean
    public EventBus shopRegistrationEventBus(
            @Qualifier("shopInitializationExecutor") ThreadPoolTaskExecutor executor) {
        return new AsyncEventBus(executor, (exception, context) -> {
            ShopRegisterEvent registration = context.getEvent() instanceof ShopRegisterEvent event ? event
                    : context.getEvent() instanceof ShopRegisterEvent.EmployeeInitialized initialized
                    ? initialized.registration() : null;
            if (registration == null) {
                log.error("初始化总线收到不支持的订阅事件", exception);
                return;
            }
            log.error("异步初始化失败 shopId={} attempt={}", registration.shopId(), registration.attempt(), exception);
            registration.fail(exception);
        });
    }
}
