package com.lrm.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

/**
 * @author 网络
 */
public class JWTUtils {


    /**
     * 随机盐
     */
    private static final String TOKEN = "t~1o33k2en!Q@W3e4r";

    /**
     * 生成token
     *
     * @param map  传入payload
     * @return 返回token
     */
    public static String getToken(Map<String, String> map){
        JWTCreator.Builder builder = JWT.create();
        //添加到token
        map.forEach(builder::withClaim);
        //1小时之后过期
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR_OF_DAY, 10);
        //添加到token
        builder.withExpiresAt(instance.getTime());
        //添加标签 返回token
        return builder.sign(Algorithm.HMAC256(TOKEN));
    }

    /**
     * 验证token
     *
     * @param token 令牌
     */
    public static void verify(String token){
        JWT.require(Algorithm.HMAC256(TOKEN)).build().verify(token);
    }

    /**
     * @param token 令牌
     * @return 获取token中payload
     */
    public static DecodedJWT getToken(String token){
        return JWT.require(Algorithm.HMAC256(TOKEN)).build().verify(token);
    }
}
