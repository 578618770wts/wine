package com.personal.wine.constants;

/**
 * 错误码定义。
 * 成功：1，失败全部负数
 */
public enum ErrorCode {
    SUCCESS(1, "成功"),
    MISSING_PARAMETER(-1, "缺少参数"),
    USER_NOT_EXIST(-2,"用户不存在"),
    DEVICE_BIND_ERROR(-3,"设备不属于该用户"),
    USER_EXIST(-4,"用户已存在，请直接登录"),
    PASSWORD_ERROR(-5,"用户名或密码错误"),
    ;



    ErrorCode(int code, String errMsg) {
        this.code = code;
        this.errMsg = errMsg;
    }

    private int code;
    private String errMsg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
