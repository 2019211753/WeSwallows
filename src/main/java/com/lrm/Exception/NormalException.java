package com.lrm.Exception;

/**
 * 类<code>Doc</code>用于：TODO
 *
 * @author 山水夜止
 * @version 1.0
 * @date 2021-05-26
 */
public class NormalException extends RuntimeException {
    protected String errorMsg;

    public NormalException() {

    }

    public NormalException(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
