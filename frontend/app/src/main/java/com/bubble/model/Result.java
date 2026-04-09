package com.bubble.model;

import com.google.gson.annotations.SerializedName;

public class Result<T> {
    @SerializedName("code")
    private Integer code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
