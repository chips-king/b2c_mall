package com.b2cmall.common.exception;

/** 可向调用方展示的业务失败；RuntimeException 保证事务按默认规则回滚。 */
public class BusinessException extends RuntimeException {
    private final int status;

    public BusinessException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() { return status; }
}
