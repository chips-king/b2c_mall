package com.b2cmall.shop.service.event;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;

/** 每次发布独享结果，只携带店铺主键和处理状态，不携带密码或哈希。 */
public final class ShopRegisterEvent {
    private enum Stage { CREATED, DISPATCHING, EMPLOYEE_SAVED, COMPLETED, FAILED }
    private final Long shopId;
    private final long attempt;
    private final Runnable onSuccess;
    private final Consumer<Throwable> onFailure;
    private final CompletableFuture<Void> completion = new CompletableFuture<>();
    private Stage stage = Stage.CREATED;

    public ShopRegisterEvent(Long shopId, long attempt, Runnable onSuccess, Consumer<Throwable> onFailure) {
        if (shopId == null || shopId <= 0) { throw new IllegalArgumentException("店铺ID无效"); }
        this.shopId = shopId;
        if (attempt <= 0) { throw new IllegalArgumentException("初始化执行编号无效"); }
        this.attempt = attempt;
        this.onSuccess = Objects.requireNonNull(onSuccess);
        this.onFailure = Objects.requireNonNull(onFailure);
    }

    public Long shopId() { return shopId; }
    public long attempt() { return attempt; }
    public CompletableFuture<Void> completion() { return completion; }

    public void obsolete() {
        fail(new CancellationException("初始化执行已超时或被替代"));
    }

    synchronized void begin() {
        requireStage(Stage.CREATED);
        stage = Stage.DISPATCHING;
    }

    public synchronized void employeeSaved() {
        requireStage(Stage.DISPATCHING);
        stage = Stage.EMPLOYEE_SAVED;
    }

    public synchronized void welcomeSaved() {
        requireStage(Stage.EMPLOYEE_SAVED);
        // 异步 post 返回不等于完成；最后一个订阅者在本地事务提交后持久化终态。
        onSuccess.run();
        stage = Stage.COMPLETED;
        completion.complete(null);
    }

    public synchronized void fail(Throwable cause) {
        Objects.requireNonNull(cause);
        if (stage == Stage.FAILED || stage == Stage.COMPLETED) { return; }
        stage = Stage.FAILED;
        try { onFailure.accept(cause); }
        finally { completion.completeExceptionally(cause); }
    }

    private void requireStage(Stage expected) {
        if (stage != expected) { throw new IllegalStateException("注册事件阶段异常：" + stage + "，预期：" + expected); }
    }

    /** 管理员成功后才发布此事件，以事件因果关系保证顺序，而非依赖订阅者注册顺序。 */
    public record EmployeeInitialized(ShopRegisterEvent registration) {
        public EmployeeInitialized { Objects.requireNonNull(registration); }
    }
}
