package com.lrm.interceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrm.util.JWTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于路径判断处理异常，拦截有问题的登录行为
 *
 * @author 山水夜止
 */
public class JWTInterceptor extends HandlerInterceptorAdapter
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        Map<String, Object> map = new HashMap<>();
        try {
            String requestURL = request.getRequestURI();
            logger.info("RequestURL: {} ", requestURL);
            logger.info("GetMethod: {}", handler);
            JWTUtils.verify(token);
            return true;
        //异常处理
        } catch (TokenExpiredException e) {
            logger.error("Exception : {}", "拦截器：用户令牌已经过期");
            map.put("isSuccess", false);
            map.put("msg", "用户令牌已经过期，请重新登陆");
            map.put("code", "401");
        } catch (SignatureVerificationException e){
            logger.error("Exception : {}", "拦截器：签名错误");
            map.put("isSuccess", false);
            map.put("msg", "签名错误");
            map.put("code", "401");
        } catch (AlgorithmMismatchException e){
            logger.error("Exception : {}", "拦截器：加密算法不匹配");
            map.put("isSuccess", false);
            map.put("msg", "加密算法不匹配");
            map.put("code", "401");
        } catch (Exception e) {
            logger.error("Exception : {}", "拦截器：无效令牌");
            map.put("isSuccess", false);
            map.put("msg", "无效令牌");
            map.put("code", "401");
        }
        //转化为json返回前端
        String json = new ObjectMapper().writeValueAsString(map);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);

        return false;
    }
}
