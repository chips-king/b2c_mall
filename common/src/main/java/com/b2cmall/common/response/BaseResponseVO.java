package com.b2cmall.common.response;

/** 沿用课堂的 status/message/data 结构；错误时 status 与 HTTP 状态一致。 */
public class BaseResponseVO<T> {
    private int status;
    private String message;
    private T data;

    public BaseResponseVO() {
    }

    public BaseResponseVO(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponseVO<T> success(T data) {
        return new BaseResponseVO<>(200, "成功", data);
    }

    public static BaseResponseVO<Void> failure(int status, String message) {
        return new BaseResponseVO<>(status, message, null);
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
