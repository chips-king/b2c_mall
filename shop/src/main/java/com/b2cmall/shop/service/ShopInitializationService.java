package com.b2cmall.shop.service;

import com.b2cmall.common.exception.BusinessException;
import com.b2cmall.shop.dao.mapper.ShopMapper;
import com.b2cmall.shop.dao.po.ShopInitializationTaskPO;
import com.b2cmall.shop.dao.po.ShopPO;
import com.b2cmall.shop.service.event.ShopRegisterEventPublisher;
import com.b2cmall.shop.web.request.RetryInitializationRequestVO;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** HTTP 编排只验证身份、领取任务并提交事件，业务步骤在受管理的异步线程中执行。 */
@Service
public class ShopInitializationService {
    public static final String PENDING = ShopInitializationStateService.PENDING;
    public static final String COMPLETED = ShopInitializationStateService.COMPLETED;
    public static final String FAILED = ShopInitializationStateService.FAILED;
    private final ShopMapper mapper;
    private final ShopRegisterEventPublisher publisher;
    private final ShopInitializationStateService states;
    private final PasswordEncoder encoder;

    public ShopInitializationService(ShopMapper mapper, ShopRegisterEventPublisher publisher,
                                    ShopInitializationStateService states, PasswordEncoder encoder) {
        this.mapper = mapper;
        this.publisher = publisher;
        this.states = states;
        this.encoder = encoder;
    }

    public record Submission(int status, String message, Map<String, Object> data) { }

    public Submission initialize(Long shopId) {
        requireCommitted();
        return submit(states.claimInitial(shopId));
    }

    public Map<String, Object> status(Long shopId) { return responseData(states.find(shopId)); }

    public Submission retry(Long shopId, RetryInitializationRequestVO request) {
        requireCommitted();
        states.find(shopId);
        ShopPO shop = mapper.findById(shopId);
        // 使用注册时的 BCrypt 哈希校验，不能让仅知道 shopId 的请求触发他人的初始化。
        boolean matches = encoder.matches(request.getAdminPassword(), shop.getAdminPassword());
        if (!shop.getAdminAccount().equals(request.getAdminAccount()) || !matches) {
            throw new BusinessException(401, "管理员账号或密码错误");
        }
        return submit(states.claimRetry(shopId));
    }

    private Submission submit(ShopInitializationStateService.Claim claim) {
        ShopInitializationTaskPO task = claim.task();
        if (claim.acquired()) {
            try { publisher.publish(task.getShopId(), task.getAttempt()); }
            catch (RejectedExecutionException exception) {
                return new Submission(503, "初始化队列已满，可查询状态后重试", responseData(states.find(task.getShopId())));
            }
        }
        int status = COMPLETED.equals(task.getInitStatus()) ? 200 : PENDING.equals(task.getInitStatus()) ? 202 : 503;
        String message = status == 200 ? "初始化已完成" : status == 202 ? "初始化已受理" : "初始化失败，可验证身份后重试";
        // 返回领取时已经落库的状态快照；202 只表示受理，最终结果以查询接口为准。
        return new Submission(status, message, responseData(task));
    }

    private Map<String, Object> responseData(ShopInitializationTaskPO task) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("shopId", task.getShopId());
        ShopPO shop = mapper.findById(task.getShopId());
        data.put("shopName", shop.getShopName());
        data.put("state", task.getInitStatus());
        data.put("employeeInitialized", task.isEmployeeInitialized());
        data.put("welcomeMessageCreated", mapper.welcomeCount(task.getShopId()) > 0);
        data.put("attempt", task.getAttempt());
        if (task.getFailureCode() != null) { data.put("failureCode", task.getFailureCode()); }
        return data;
    }

    private void requireCommitted() {
        if (TransactionSynchronizationManager.isSynchronizationActive()
                || TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalTransactionStateException("店铺事务提交后才能执行初始化");
        }
    }
}
