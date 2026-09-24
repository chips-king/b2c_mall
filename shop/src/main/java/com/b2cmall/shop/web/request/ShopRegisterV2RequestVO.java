package com.b2cmall.shop.web.request;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ShopRegisterV2RequestVO extends ShopRegisterRequestVO {
    @Size(max=32) @Pattern(regexp="[A-Za-z0-9_-]+")
    private String source;
    @Size(max=64) @Pattern(regexp="[A-Za-z0-9_-]+")
    private String invaliCode;
    public String getSource() { return source; }
    public void setSource(String value) { source = value; }
    public String getInvaliCode() { return invaliCode; }
    public void setInvaliCode(String value) { invaliCode = value; }
}
