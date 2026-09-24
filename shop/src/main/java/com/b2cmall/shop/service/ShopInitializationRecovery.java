package com.b2cmall.shop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 扫描持久化截止时间，进程异常退出后的任务也能在重启后进入可重试状态。 */
@Component
public class ShopInitializationRecovery {
    private static final Logger log = LoggerFactory.getLogger(ShopInitializationRecovery.class);
    private final ShopInitializationStateService states;

    public ShopInitializationRecovery(ShopInitializationStateService states) { this.states = states; }

    @Scheduled(fixedDelayString = "${mall.initialization.scan-interval-ms:5000}")
    public void recover() {
        int count = states.expireOverdue();
        if (count > 0) { log.warn("初始化任务超时转为 FAILED，数量={}", count); }
    }
}
