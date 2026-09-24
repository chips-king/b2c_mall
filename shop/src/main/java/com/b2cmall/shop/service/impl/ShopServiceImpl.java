package com.b2cmall.shop.service.impl;

import com.b2cmall.common.exception.BusinessException;
import com.b2cmall.shop.dao.mapper.ShopMapper;
import com.b2cmall.shop.dao.po.ShopPO;
import com.b2cmall.shop.service.ShopService;
import com.b2cmall.shop.service.ShopInitializationStateService;
import com.b2cmall.shop.service.ShopRegisterDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopServiceImpl implements ShopService {
    private final ShopMapper shopMapper;
    private final PasswordEncoder passwordEncoder;
    private final ShopInitializationStateService initializationStates;

    public ShopServiceImpl(ShopMapper shopMapper, PasswordEncoder passwordEncoder, ShopInitializationStateService initializationStates) {
        this.shopMapper = shopMapper;
        this.passwordEncoder = passwordEncoder;
        this.initializationStates = initializationStates;
    }

    @Override
    @Transactional
    public Long register(ShopRegisterDTO request) {
        ShopPO shop = new ShopPO();
        shop.setShopName(request.getShopName());
        shop.setAdminAccount(request.getAdminAccount());
        shop.setAdminPassword(passwordEncoder.encode(request.getAdminPassword()));

        // 首条SQL尝试插入，利用唯一约束串行处理同名并发；冲突时校验原管理员凭据。
        int rows = shopMapper.registerShop(shop);
        if (rows == 0) {
            ShopPO existing = shopMapper.findByName(request.getShopName());
            if (existing == null || !existing.getAdminAccount().equals(request.getAdminAccount())
                    || !passwordEncoder.matches(request.getAdminPassword(), existing.getAdminPassword())) {
                throw new BusinessException(409, "店铺名已存在且管理员凭据不一致");
            }
            return existing.getId();
        }
        if (rows != 1 || shop.getId() == null || shop.getId() <= 0) {
            throw new IllegalStateException("保存店铺后未获得有效主键");
        }
        // 店铺与待处理任务原子提交；提交后即使进程中断，任务也能被超时扫描找到。
        initializationStates.createTask(shop.getId());
        return shop.getId();
    }
}
