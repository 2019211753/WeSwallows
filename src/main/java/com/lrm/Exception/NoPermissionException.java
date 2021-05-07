package com.lrm.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 访问未被授权资源时抛出的异常类
 *
 * @author 山水夜止
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NoPermissionException extends RuntimeException {
    protected Integer errorCode;
    protected String errorMsg;

    public NoPermissionException() {

    }

    public NoPermissionException(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public NoPermissionException(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
