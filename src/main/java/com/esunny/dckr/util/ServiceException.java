package com.esunny.dckr.util;

/**
 * @author: 李先生
 * @description: 自定义Service异常
 * @Version 1.0
 * @create: 2021-02-13 10:32
 **/
public class ServiceException extends Exception {

    public ServiceException(){
        super();
    }

    public ServiceException(String message, Throwable cause){
        super(message, cause);
    }

    public ServiceException(String message){
        super(message);
    }

    public ServiceException(Throwable cause){
        super(cause);
    }
}
