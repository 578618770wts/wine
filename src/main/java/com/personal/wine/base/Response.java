package com.personal.wine.base;




import com.personal.wine.constants.ErrorCode;

import java.io.Serializable;

public class Response<T> implements Serializable {
    private ErrorCode errorCode = ErrorCode.SUCCESS;
    private T data;

    public Response() {
    }

    public Response(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public Response(ErrorCode errorCode, T data) {
        this.errorCode = errorCode;
        this.data = data;
    }

    public Response<T> setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public int getCode() {
        return errorCode.getCode();
    }

    public String getErrMsg() {
        return errorCode.getErrMsg();
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "errorCode=" + errorCode +
                ", data=" + data +
                '}';
    }
}
