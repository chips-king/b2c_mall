package com.b2cmall.shop.service.event.handler;

import com.b2cmall.common.response.BaseResponseVO;
import com.b2cmall.shop.dao.mapper.ShopMapper;
import com.b2cmall.shop.dao.po.ShopPO;
import com.b2cmall.shop.feign.EmployeeFeignClient;
import com.b2cmall.shop.feign.request.AddEmployeeRequestVO;
import com.b2cmall.shop.service.event.ShopRegisterEvent;
import com.b2cmall.shop.service.event.ShopRegisterObserver;
import com.b2cmall.shop.service.ShopInitializationStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.AllowConcurrentEvents;
import org.springframework.stereotype.Component;

@Component
public class InitEmployeeEventHandler implements ShopRegisterObserver {
    private static final Logger log = LoggerFactory.getLogger(InitEmployeeEventHandler.class);
    private final ShopMapper mapper;
    private final EmployeeFeignClient client;
    private final String token;
    private final EventBus eventBus;
    private final ShopInitializationStateService states;

    public InitEmployeeEventHandler(ShopMapper mapper, EmployeeFeignClient client, EventBus eventBus,
                                   ShopInitializationStateService states,
                                   @Value("${mall.internal-service-token}") String token) {
        if (token == null || token.isBlank()) { throw new IllegalArgumentException("INTERNAL_SERVICE_TOKEN 不能为空"); }
        this.mapper = mapper;
        this.client = client;
        this.token = token;
        this.eventBus = eventBus;
        this.states = states;
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onRegistered(ShopRegisterEvent event) {
        if (!states.isActive(event.shopId(), event.attempt())) { event.obsolete(); return; }
        ShopPO shop = mapper.findById(event.shopId());
        if (shop == null) { throw new IllegalStateException("初始化管理员时店铺不存在"); }
        AddEmployeeRequestVO request = new AddEmployeeRequestVO();
        request.setShopId(shop.getId());
        request.setUsername(shop.getAdminAccount());
        request.setPasswordHash(shop.getAdminPassword());
        BaseResponseVO<Long> response = client.save(token, request);
        if (response == null || response.getStatus() != 200 || response.getData() == null || response.getData() <= 0) {
            throw new IllegalStateException("Employee 未返回成功状态和有效员工ID");
        }
        log.info("管理员初始化成功 shopId={} employeeId={}", shop.getId(), response.getData());
        // 仅在真实员工 ID 校验通过后触发消息步骤；每个请求携带独立状态。
        // 远程调用期间可能超时并被重试；旧执行不再触发后续步骤，已提交员工由唯一约束复用。
        if (!states.isActive(event.shopId(), event.attempt())) { event.obsolete(); return; }
        states.employeeInitialized(event.shopId(), event.attempt());
        event.employeeSaved();
        eventBus.post(new ShopRegisterEvent.EmployeeInitialized(event));
    }
}
