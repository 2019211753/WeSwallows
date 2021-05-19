package com.lrm.util;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lrm.po.User;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 山水夜止.
 */
public class TokenInfo {
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
     * @param request 获得当前token
     * @return 得到当前用户昵称
     * @throws JWTVerificationException JWT鉴权错误
     */
    public static String getCustomNickname(HttpServletRequest request) throws JWTVerificationException {
        String token = request.getHeader("token");
        DecodedJWT decodedJWT = JWTUtils.getToken(token);
        //注意！！！这里登陆时转化为token的map中是什么数据类型，取出来就得是什么类型！！不能直接asLong!
        String nickname = decodedJWT.getClaim("nickname").asString();
        return nickname;
    }

    /**
     * 验证是否是管理页
     *
     * @param request 获得当前token
     * @return true是 false不是
     * @throws JWTVerificationException JWT鉴权错误
     */
    public static Boolean isAdmin(HttpServletRequest request) throws JWTVerificationException {
        String token = request.getHeader("token");
        DecodedJWT decodedJWT = JWTUtils.getToken(token);
        Boolean isAdmin = Boolean.parseBoolean(decodedJWT.getClaim("admin").asString());
        return isAdmin;
    }

    /**
     * @param user 当前用户对象
     * @return token
     */
    public static String postToken(User user) {
        Map<String, String> map = new HashMap<>(5);

        //把这些字段放在请求头里 其他东西在需要的时候可以另外返回
        //注意！！！这里放进去map是什么数据类型，取出来就得是什么类型！！
        map.put("userId", user.getId().toString());
        map.put("nickname", user.getNickname());
        map.put("avatar", user.getAvatar());
        map.put("admin", user.getAdmin().toString());
        map.put("canSpeak", user.getCanSpeak().toString());
        String token = JWTUtils.getToken(map);

        return token;
    }
}
