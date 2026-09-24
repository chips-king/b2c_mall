package com.b2cmall.shop.service;

import com.b2cmall.common.exception.BusinessException;
import com.b2cmall.shop.dao.mapper.ShopMapper;
import com.b2cmall.shop.dao.po.ShopInitializationTaskPO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** 只处理短数据库事务；线程调度、BCrypt 和远程调用均在事务外执行。 */
@Service
public class ShopInitializationStateService {
    public static final String PENDING = "PENDING";
    public static final String COMPLETED = "COMPLETED";
    public static final String FAILED = "FAILED";
    private final ShopMapper mapper;
    private final long timeoutMillis;

    public ShopInitializationStateService(ShopMapper mapper,
            @Value("${mall.initialization.timeout-seconds:60}") long timeoutSeconds) {
        if (timeoutSeconds <= 0) { throw new IllegalArgumentException("初始化超时必须大于零"); }
        this.mapper = mapper;
        this.timeoutMillis = Math.multiplyExact(timeoutSeconds, 1000L);
    }

    public record Claim(ShopInitializationTaskPO task, boolean acquired) { }

    @Transactional(propagation = Propagation.MANDATORY)
    public void createTask(Long shopId) {
        requireOne(mapper.createInitializationTask(shopId, deadline()));
    }

    public ShopInitializationTaskPO find(Long shopId) {
        if (shopId == null || shopId <= 0) { throw new BusinessException(400, "店铺ID无效"); }
        ShopInitializationTaskPO task = mapper.findInitializationTask(shopId);
        if (task == null) { throw new BusinessException(404, "店铺初始化任务不存在"); }
        return task;
    }

    @Transactional
    public Claim claimInitial(Long shopId) { return claim(shopId, false); }

    @Transactional
    public Claim claimRetry(Long shopId) { return claim(shopId, true); }

    private Claim claim(Long shopId, boolean retry) {
        ShopInitializationTaskPO task = find(shopId);
        if (PENDING.equals(task.getInitStatus()) && task.getDeadlineEpochMs() <= System.currentTimeMillis()) {
            finishCurrent(task, FAILED, "TIMEOUT");
            task = find(shopId);
        }
        boolean eligible = retry ? FAILED.equals(task.getInitStatus())
                : PENDING.equals(task.getInitStatus()) && task.getAttempt() == 0;
        if (!eligible) { return new Claim(task, false); }
        requireOne(mapper.advanceInitializationTask(shopId, task.getAttempt(), deadline()));
        requireOne(mapper.changeInitializationStatus(shopId, task.getInitStatus(), PENDING));
        return new Claim(find(shopId), true);
    }

    @Transactional
    public void employeeInitialized(Long shopId, long attempt) {
        if (mapper.markEmployeeInitialized(shopId, attempt) != 1) { throw new IllegalStateException("初始化执行已被替代"); }
    }

    public boolean isActive(Long shopId, long attempt) {
        ShopInitializationTaskPO task = find(shopId);
        return task.getAttempt() == attempt && PENDING.equals(task.getInitStatus())
                && task.getDeadlineEpochMs() > System.currentTimeMillis();
    }

    @Transactional
    public boolean finish(Long shopId, long attempt, boolean success, String failureCode) {
        ShopInitializationTaskPO task = find(shopId);
        if (task.getAttempt() != attempt || !PENDING.equals(task.getInitStatus())) { return false; }
        boolean expired = task.getDeadlineEpochMs() <= System.currentTimeMillis();
        // 超时后即使旧执行返回成功，也只能成为超时失败，由下一次重试复用已完成的业务记录。
        return finishCurrent(task, success && !expired ? COMPLETED : FAILED,
                expired ? "TIMEOUT" : success ? null : failureCode);
    }

    @Transactional
    public int expireOverdue() {
        int expired = 0;
        for (ShopInitializationTaskPO task : mapper.findExpiredInitializationTasks(System.currentTimeMillis())) {
            if (finishCurrent(task, FAILED, "TIMEOUT")) { expired++; }
        }
        return expired;
    }

    private boolean finishCurrent(ShopInitializationTaskPO task, String status, String failureCode) {
        int changed = mapper.finishInitialization(task.getShopId(), task.getAttempt(), status);
        if (changed == 0) { return false; }
        requireOne(changed);
        requireOne(mapper.recordInitializationFailure(task.getShopId(), task.getAttempt(), failureCode));
        return true;
    }

    private long deadline() { return Math.addExact(System.currentTimeMillis(), timeoutMillis); }
    private void requireOne(int rows) {
        if (rows != 1) { throw new IllegalStateException("初始化状态更新失败，事务回滚"); }
    }
}
