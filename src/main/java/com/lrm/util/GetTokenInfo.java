package com.lrm.util;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 山水夜止.
 */
public class GetTokenInfo {
    /**
     * @param request 得到当前token
     * @return 返回当前UserId
     * @throws JWTVerificationException JWT鉴权错误
     */
    public static Long getCustomUserId(HttpServletRequest request) throws JWTVerificationException {
        String token = request.getHeader("token");
        DecodedJWT decodedJWT = JWTUtils.getToken(token);
        //注意！！！这里登陆时转化为token的map中是什么数据类型，取出来就得是什么类型！！不能直接asLong!
        Long userId = Long.parseLong(decodedJWT.getClaim("userId").asString());
        return userId;
    }

    /**
     * 验证是否是管理页
     *
     * @param request 获得当前token
     * @return true是 false不是
     * @throws JWTVerificationException JWT鉴权错误
     */
    public static Boolean isAdmin(HttpServletRequest request) throws JWTVerificationException
    {
        String token = request.getHeader("token");
        DecodedJWT decodedJWT = JWTUtils.getToken(token);
        Boolean isAdmin = Boolean.parseBoolean(decodedJWT.getClaim("isAdmin").asString());
        return isAdmin;
    }

}
