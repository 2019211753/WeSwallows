package com.lrm.vo;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lrm.Exception.NoPermissionException;
import com.lrm.Exception.NormalException;
import com.lrm.Exception.NotFoundException;

import java.io.IOException;

//包装类

//406定义为操作失误（不是跳转到异常页面，而是直接在当前页给出提示）
//404定义为没找到对应资源
//403定义为无权限访问
//402定义为文件过大
//401定义为JWT鉴权失败
//400定义其他未知错误

//200为操作成功

public class Result<T> {

    T data;

    Integer code;

    String msg;

    StringBuffer url;

    //正常类型的包装返回的结果
    public Result(T data, String msg) {
        this.setData(data);
        this.setCode(200);
        this.setMsg(msg);
    }

    //必须存在一个无参构造方法^^
    public Result() {

    }

    //自定义异常返回的结果
    //为什么不用泛型一次解决所有自定义异常？如果用泛型的话，参数就不能用异常类来封装，那么业务层只有throws出的对象有用，还需要在ControllerAdvice层定义msg和code，不如一次性抛出，感觉复杂程度差不多？
    //为什么static？因为调用的时候不需要初始化泛型 只有设成static方法才可以

    public static Result returnParameterException(String msg) {
        Result result = new Result();
        result.setData(null);
        result.setMsg(msg);
        result.setCode(406);
        return result;
    }

    public static Result returnNormalException(NormalException normalException) {
        Result result = new Result();
        result.setData(null);
        result.setMsg(normalException.getErrorMsg());
        result.setCode(406);
        return result;
    }

    public static Result returnNotFoundException(NotFoundException notFoundException, StringBuffer url) {
        Result result = new Result();
        result.setData(null);
        result.setMsg(notFoundException.getErrorMsg());
        result.setUrl(url);
        result.setCode(404);
        return result;
    }

    public static Result returnNoPermissionException(NoPermissionException noPermissionException, StringBuffer url) {
        Result result = new Result();
        result.setData(null);
        result.setMsg(noPermissionException.getErrorMsg());
        result.setUrl(url);
        result.setCode(403);
        return result;
    }

    public static Result returnIOException() {
        Result result = new Result();
        result.setData(null);
        result.setMsg("文件超过了1MB");
        result.setCode(402);
        return result;
    }

    public static Result returnJWTException(StringBuffer url) {
        Result result = new Result();
        result.setData(null);
        result.setMsg("用户令牌无效");
        result.setUrl(url);
        result.setCode(401);
        return result;
    }

    //其他未知异常返回的结果
    public static Result returnNotDefinedError(StringBuffer url){
        Result result = new Result();
        result.setData(null);
        result.setMsg("我也不知道发生甚么事了...");
        result.setUrl(url);
        result.setCode(400);
        return result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
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

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Result{" +
                "data=" + data +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                ", url=" + url +
                '}';
    }

}