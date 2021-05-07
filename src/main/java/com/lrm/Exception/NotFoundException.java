package com.lrm.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 访问不存在的资源时抛出的异常类
 *
 * @author 山水夜止
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException
{
    protected Integer errorCode;
    protected String errorMsg;

    public NotFoundException(){

    }

    public NotFoundException(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public NotFoundException(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }


    @Override
    public String toString() {
        return "NotFoundException{" +
                "errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
