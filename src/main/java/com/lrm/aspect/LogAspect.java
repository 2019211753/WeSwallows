package com.lrm.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author 山水夜止
 */
@Aspect
@Component
public class LogAspect {
    /**
     * 随着类加载初始化 获得日志对象
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * log()仅作为连接点签名
     */
    @Pointcut("execution(* com.lrm.web.*.*(..))")
    public void log() {
    }

    /**
     * 相当于 @Before("execution(*  com.lrm.web.*.*(..))")
     *
     * @param joinPoint 方法
     */
    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();
        String ip = request.getRemoteAddr();
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        RequestLog requestLog = new RequestLog(url, ip, classMethod, args);
        logger.info("Request: {}", requestLog);
    }

    @After("log()")
    public void doAfter() {
        logger.info("------doAfter-----");
    }

    @AfterReturning(pointcut = "log()", returning = "result")
    public void doAfterReturning(Object result) {
        logger.info("Result : {}", result);
    }

    @AfterThrowing(value = "log()", throwing = "exception")
    public void afterThrowing(JoinPoint jp, Exception exception) {
        String name = jp.getSignature().getName();
        logger.info("Exception : {}", name + "方法抛异常了，" + exception.getMessage());
    }

    private class RequestLog {
        private String url;
        private String ip;
        private String classMethod;
        private Object[] args;

        public RequestLog(String url, String ip, String classMethod, Object[] args) {
            this.url = url;
            this.ip = ip;
            this.classMethod = classMethod;
            this.args = args;
        }

        @Override
        public String toString() {
            return "RequestLog{" +
                    "url='" + url + '\'' +
                    ", ip='" + ip + '\'' +
                    ", classMethod='" + classMethod + '\'' +
                    ", args=" + Arrays.toString(args) +
                    '}';
        }
    }
}
