package com.b2cmall.shop.service.event.handler;

import com.b2cmall.shop.service.MessageService;
import com.b2cmall.shop.service.ShopInitializationStateService;
import com.b2cmall.shop.service.event.ShopRegisterEvent;
import com.b2cmall.shop.service.event.ShopRegisterObserver;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.AllowConcurrentEvents;
import org.springframework.stereotype.Component;

@Component
public class InitShopMessageEventHandler implements ShopRegisterObserver {
    private final MessageService service;
    private final ShopInitializationStateService states;

    public InitShopMessageEventHandler(MessageService service, ShopInitializationStateService states) {
        this.service = service;
        this.states = states;
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onRegistered(ShopRegisterEvent.EmployeeInitialized initialized) {
        ShopRegisterEvent event = initialized.registration();
        if (!states.isActive(event.shopId(), event.attempt())) { event.obsolete(); return; }
        service.saveWelcomeMessage(event.shopId());
        event.welcomeSaved();
    }
}
