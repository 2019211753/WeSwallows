package com.lrm.web;

import com.lrm.po.User;
import com.lrm.service.UserService;
import com.lrm.util.JWTUtils;
import com.lrm.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
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
@RestController
public class LoginController {
    @Autowired
    private UserService userService;

    /**
     * 注册
     *
     * @param result 校验异常处理
     * @param user   前端封装好的User对象 包含用户名、密码、昵称
     * @return 返回注册成功得到的User对象 需要确定泛型，否则操作无效了; 返回注册失败的报错信息
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid User user, BindingResult result)
    {
        Map<String, Object> hashMap = new HashMap<>(1);

        //校验
        if(result.hasErrors())
        {
            List<FieldError> errors = result.getFieldErrors();
            StringBuilder buffer = new StringBuilder(64);
            for (ObjectError error : errors)
            {
                buffer.append(error.getDefaultMessage()).append("；");
            }

            return new Result<>(null, false, new String(buffer));
        }

        String username = user.getUsername();
        String password = user.getPassword();
        String nickname = user.getNickname();

        //先检查是否已经注册过。注册过报错；没注册过注册，注册成功跳转到登录。
        User user1 = userService.checkRegister(username, nickname);
        if(user1 != null)
        {
            //跳转到注册页面
            return new Result<>(null, false, "该用户名或昵称已被注册过");
        } else {

            userService.saveUser(username, password, nickname);

            //封装成游离态的User对象 不要返回密码到前端 其实在JsonIgnore之后就没用了
            user.setPassword(null);

            //又给它整回去
            hashMap.put("user", user);

            //跳转到登录页面
            return new Result<>(hashMap, true, "注册成功");
        }
    }

    /**
     * 登录
     *
     * @param username 用户名
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
            errorMsg.append("请输入用户名；");
            return new Result<>(null, false, new String(errorMsg));

        } else if (username == null & password == null) {
            errorMsg.append("请输入用户名；");
        }
        if (password == null)
        {
            errorMsg.append("请输入密码；");
            return new Result<>(null, false, new String(errorMsg));
        }

        //检查用户名和密码在数据库中存在不。(不考虑是否注册过了)。存在登录；不存在报错。
        User user1 = userService.checkUser(username, password);
        if(user1 != null)
        {
            Map<String, String> map = new HashMap<>(5);

            //把这些字段放在请求头里 其他东西在需要的时候可以另外返回
            //注意！！！这里放进去map是什么数据类型，取出来就得是什么类型！！
            map.put("userId", user1.getId().toString());
            map.put("nickname", user1.getNickname());
            map.put("avatar", user1.getAvatar());
            map.put("admin", user1.getAdmin().toString());
            map.put("canSpeak", user1.getCanSpeak().toString());
            String token = JWTUtils.getToken(map);

            hashMap.put("token", token);
            //返回首页
            return new Result<>(hashMap, true, "登录成功");
        } else {
            //返回登录页面
            return new Result<>(null, false,"用户名或密码错误");
        }
    }
}
