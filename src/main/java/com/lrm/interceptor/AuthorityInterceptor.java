package com.lrm.interceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
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
 * 基于路径判断处理异常, 拦截普通用户进入管理页的行为
 *
 * @author 山水夜止
 */
public class AuthorityInterceptor extends HandlerInterceptorAdapter
{
    @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            //在请求头中取得token

            String token = request.getHeader("token");
            Map<String, Object> map = new HashMap<>();
            try {
                //这里发生异常需要后面来处理
                JWTUtils.verify(token);
                DecodedJWT decodedJWT = JWTUtils.getToken(token);
                //在token中取得admin
                boolean admin = Boolean.parseBoolean(decodedJWT.getClaim("admin").asString());
                if (!admin) {
                    map.put("isSuccess", false);
                    map.put("msg", "您没有管理员权限");
                    map.put("code", "403");
                } else {
                    return true;
                }
                //异常处理
            } catch (TokenExpiredException e) {
                map.put("isSuccess", false);
                map.put("msg", "用户令牌已经过期，请重新登陆");
                map.put("code", "401");
            } catch (SignatureVerificationException e){
                map.put("isSuccess", false);
                map.put("msg", "签名错误");
                map.put("code", "401");
            } catch (AlgorithmMismatchException e){
                map.put("isSuccess", false);
                map.put("msg", "加密算法不匹配");
                map.put("code", "401");
            } catch (Exception e) {
                map.put("isSuccess", false);
                map.put("msg", "无效令牌");
                map.put("code", "401");
            }
            //转化为json返回前端
            //方法声明时的throws Exception是抛出这里的异常
            String json = new ObjectMapper().writeValueAsString(map);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(json);

            return false;
        }
    }
