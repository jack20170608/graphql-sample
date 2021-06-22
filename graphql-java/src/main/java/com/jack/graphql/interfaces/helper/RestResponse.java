package com.jack.graphql.interfaces.helper;

import java.io.Serializable;

public class RestResponse<T> implements Serializable {

    //The status code
    private int code;

    private String message;

    private T data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public RestResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return "RestResponse{" +
            "code=" + code +
            ", message='" + message + '\'' +
            ", data=" + data +
            '}';
    }
}
