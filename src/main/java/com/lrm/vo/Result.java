package com.lrm.vo;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lrm.Exception.NoPermissionException;
import com.lrm.Exception.NotFoundException;

import java.io.IOException;

//包装类
//404定义为没找到对应资源
//403定义为无权限访问
//402定义为文件过大
//401定义为JWT鉴权失败
//400定义其他未知错误

public class Result <T> {

    T data;

    Boolean isSuccess;

    Integer errorCode;

    String msg;

    StringBuffer url;

    //正常类型的包装返回的结果
    public Result(T data, Boolean isSuccess, String msg) {
        this.setData(data);
        this.setSuccess(isSuccess);
        this.setMsg(msg);
    }

    //必须存在一个无参构造方法^^
    public Result()
    {

    }

    //自定义异常返回的结果
    //为什么不用泛型一次解决所有自定义异常？如果用泛型的话，参数就不能用异常类来封装，那么业务层只有throws出的对象有用，还需要在ControllerAdvice层定义msg和code，不如一次性抛出，感觉复杂程度差不多？
    //为什么static？因为调用的时候不需要初始化泛型 只有设成static方法才可以
    public  static Result returnNoPermissionException(NoPermissionException noPermissionException, StringBuffer url){
        Result result = new Result();
        result.setData(null);
        result.setSuccess(false);
        result.setMsg(noPermissionException.getErrorMsg());
        result.setUrl(url);
        result.setErrorCode(403);
        return result;
    }

    public  static Result returnNotFoundException(NotFoundException notFoundException, StringBuffer url){
        Result result = new Result();
        result.setData(null);
        result.setSuccess(false);
        result.setMsg(notFoundException.getErrorMsg());
        result.setUrl(url);
        result.setErrorCode(404);
        return result;
    }

    public  static Result returnIOException(IOException ioException, StringBuffer url){
        Result result = new Result();
        result.setData(null);
        result.setSuccess(false);
        result.setMsg("文件超过了1MB");
        result.setUrl(url);
        result.setErrorCode(402);
        return result;
    }

    public  static Result returnJWTException(JWTVerificationException jwtVerificationException, StringBuffer url){
        Result result = new Result();
        result.setData(null);
        result.setSuccess(false);
        result.setMsg("用户令牌无效");
        result.setUrl(url);
        result.setErrorCode(401);
        return result;
    }

    //其他未知异常返回的结果
    public static Result returnNotDefinedError(StringBuffer url){
        Result result = new Result();
        result.setData(null);
        result.setSuccess(false);
        result.setMsg("我也不知道发生甚么事了...");
        result.setUrl(url);
        result.setErrorCode(400);
        return result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public StringBuffer getUrl() {
        return url;
    }

    public void setUrl(StringBuffer url) {
        this.url = url;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "Result{" +
                "data=" + data +
                ", isSuccess=" + isSuccess +
                ", errorCode=" + errorCode +
                ", msg='" + msg + '\'' +
                ", url=" + url +
                '}';
    }

}