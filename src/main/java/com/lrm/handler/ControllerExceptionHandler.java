package com.lrm.handler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lrm.Exception.NoPermissionException;
import com.lrm.Exception.NotFoundException;
import com.lrm.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 为了不将抛出的异常直接暴露给用户
 * 全局异常处理 扫描所有的controller类
 *
 * @author 山水夜止
 */

@RestControllerAdvice
public class ControllerExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 负责处理资源找不到异常
     *
     * @param request           获取请求发送的资源 URL
     * @param notFoundException 资源不存在异常类
     * @return 给前端展示的结果 code为404
     */
    @ExceptionHandler(NotFoundException.class)
    public Result NotFoundExceptionHandler(HttpServletRequest request, NotFoundException notFoundException) {
        logger.error("Request URL: {}, Exception : {}", request.getRequestURL(), notFoundException);
        return Result.returnNotFoundException(notFoundException, request.getRequestURL());
    }

    /**
     * 负责处理权限不足异常
     *
     * @param request               获取请求发送的资源 URL
     * @param noPermissionException 无权限异常类
     * @return 给前端展示的结果 code为403
     */
    @ExceptionHandler(NoPermissionException.class)
    public Result NoPermissionExceptionHandler(HttpServletRequest request, NoPermissionException noPermissionException) {
        logger.error("Request URL: {}, Exception : {}", request.getRequestURL(), noPermissionException);
        return Result.returnNoPermissionException(noPermissionException, request.getRequestURL());
    }

    /**
     * 负责处理上传文件过大异常
     *
     * @param request     获取请求发送的资源 URL
     * @param ioException 文件过大异常
     * @return 给前端展示的结果 code为402
     */
    @ExceptionHandler(IOException.class)
    public Result IOEHandler(HttpServletRequest request, IOException ioException) {
        logger.error("Request URL: {}, Exception : {}", request.getRequestURL(), ioException);
        return Result.returnIOException(ioException, request.getRequestURL());
    }

    /**
     * 负责处理JWT异常
     *
     * @param request                  获取请求发送的资源 URL
     * @param jwtVerificationException JWT鉴权异常
     * @return 给前端展示的结果 code为401
     */
    @ExceptionHandler(JWTVerificationException.class)
    public Result JWTHandler(HttpServletRequest request, JWTVerificationException jwtVerificationException) {
        logger.error("Request URL: {}, Exception : {}", request.getRequestURL(), jwtVerificationException);
        return Result.returnJWTException(jwtVerificationException, request.getRequestURL());
    }

    /**
     * 负责处理不知道拿来的异常
     *
     * @param request 获取请求发送的资源 URL
     * @param e       其他异常
     * @return 给前端展示的结果 code为400
     * @throws Exception 其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(HttpServletRequest request, Exception e) throws Exception {
        logger.error("Request URL: {}, Exception : {}", request.getRequestURL(), e);
        //排除我自定义的NotFound类 如果不加这个判断 会被error.html拦截 所以要判断一下ResponseStatus的"状态"
        if ((AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)) {
            throw e;
        }
        return Result.returnNotDefinedError(request.getRequestURL());
    }

}
