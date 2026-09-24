package com.b2cmall.shop.service;

import com.b2cmall.shop.web.request.ShopRegisterRequestVO;

/** 适配后的统一输入；来源和邀请码仅用于课堂适配演示，不作为权限或入库依据。 */
public class ShopRegisterDTO extends ShopRegisterRequestVO {
    private String source;
    private String invaliCode;
    public String getSource() { return source; }
    public void setSource(String value) { source = value; }
    public String getInvaliCode() { return invaliCode; }
    public void setInvaliCode(String value) { invaliCode = value; }
}
