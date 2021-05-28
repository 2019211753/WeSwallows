package com.lrm.web;

import com.lrm.Exception.NormalException;
import com.lrm.po.User;
import com.lrm.service.UserService;
import com.lrm.util.JWTUtils;
import com.lrm.util.TokenInfo;
import com.lrm.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 山水夜止
 */
@Validated
@RestController
public class LoginController {
    @Autowired
    private UserService userService;

    /**
     * 注册
     *
     * @param user   前端封装好的User对象 包含账号、密码、昵称
     * @return 返回注册成功得到的User对象 需要确定泛型，否则操作无效了; 返回注册失败的报错信息
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid User user) {
        Map<String, Object> hashMap = new HashMap<>(1);

        String username = user.getUsername();
        String password = user.getPassword();
        String nickname = user.getNickname();

        //先检查是否已经注册过。注册过报错；没注册过注册，注册成功跳转到登录。
        User user1 = userService.checkRegister(username, nickname);
        if (user1 != null)
        {
            //跳转到注册页面
            throw new NormalException("该账号或昵称已被注册过");
        } else {

            userService.saveUser(username, password, nickname);

            //封装成游离态的User对象 不要返回密码到前端 其实在JsonIgnore之后就没用了
            user.setPassword(null);

            //又给它整回去
            hashMap.put("user", user);

            //跳转到登录页面
            return new Result<>(hashMap, "注册成功");
        }
    }

    /**
     * 登录
     *
     * @param username 账号
     * @param password 密码
     * @return 登录成功的token; 登陆失败的报错信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestParam(required = false) String username, @RequestParam(required = false) String password) {
        //需要传递到前端的 包含在token内的信息 map用来存放payload
        //或传递报错信息
        Map<String, Object> hashMap = new HashMap<>(1);

        StringBuilder errorMsg = new StringBuilder(64);
        if (username == null & password != null) {
            errorMsg.append("请输入账号；");
            throw new NormalException(new String(errorMsg));

        } else if (username == null & password == null) {
            errorMsg.append("请输入账号；");
        }
        if (password == null)
        {
            errorMsg.append("请输入密码；");
            throw new NormalException(new String(errorMsg));
        }

        //检查账号和密码在数据库中存在不。(不考虑是否注册过了)。存在登录；不存在报错。
        User user1 = userService.checkUser(username, password);
        if(user1 != null)
        {
            String token = TokenInfo.postToken(user1);

            hashMap.put("token", token);
            //返回首页
            return new Result<>(hashMap, "登录成功");
        } else {
            //返回登录页面
            throw new NormalException("账号或密码错误");
        }
    }
}
