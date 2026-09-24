package com.b2cmall.order.enums;

import com.b2cmall.common.exception.BusinessException;

/** 课堂阶段只提供支付宝、微信两种模拟渠道。 */
public enum PayTypeEnum {
    ALIPAY, WECHAT;
    public static PayTypeEnum parse(String value) {
        if (value != null) {
            for (PayTypeEnum type : values()) { if (type.name().equals(value)) { return type; } }
        }
        throw new BusinessException(400, "支付类型必须为ALIPAY或WECHAT");
    }
}
