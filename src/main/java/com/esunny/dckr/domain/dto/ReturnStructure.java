package com.esunny.dckr.domain.dto;

/**
 * @author: 李先生
 * @description: 统一返回结构体
 * @Version 1.0
 * @create: 2021-02-13 22:19
 **/
public class ReturnStructure {
    private int code;
    private String message;
    private Object data;

    public ReturnStructure(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ReturnStructure(int code, String message) {
        this(code,message,null);
    }

    public ReturnStructure(int code) {
        this(code,null,null);
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
