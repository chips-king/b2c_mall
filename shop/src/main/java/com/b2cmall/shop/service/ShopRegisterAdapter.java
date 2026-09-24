package com.b2cmall.shop.service;

import com.b2cmall.shop.web.request.*;

/** 两个课堂版本只在输入适配上不同，后续复用同一真实注册流程。 */
public final class ShopRegisterAdapter {
    private ShopRegisterAdapter() { }
    public static ShopRegisterDTO adapt(ShopRegisterRequestVO request) {
        ShopRegisterDTO dto = new ShopRegisterDTO();
        dto.setShopName(request.getShopName()); dto.setAdminAccount(request.getAdminAccount());
        dto.setAdminPassword(request.getAdminPassword());
        dto.setSource("OLD_VER"); dto.setInvaliCode("-1");
        if (request instanceof ShopRegisterV2RequestVO v2) {
            dto.setSource(v2.getSource() == null ? "NEW_VER" : v2.getSource());
            dto.setInvaliCode(v2.getInvaliCode() == null ? "-1" : v2.getInvaliCode());
        }
        return dto;
    }
}
