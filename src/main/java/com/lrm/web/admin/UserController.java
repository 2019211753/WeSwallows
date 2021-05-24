package com.lrm.web.admin;

import com.lrm.po.User;
import com.lrm.service.UserService;
import com.lrm.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 山水夜止
 */
@RestController
@RequestMapping("/admin")
public class UserController {
    @Autowired
    UserService userService;

    /**
     * @param nickname 昵称
     * @return 查询得到的用户
     */
    @PostMapping("/searchUser")
    public Result<Map<String, Object>> searchCustomer(String nickname)
    {
        Map<String, Object> hashMap = new HashMap<>(1);

        hashMap.put("user", userService.getUser(nickname));

        return new Result<>(hashMap, true, "搜索完成");
    }

    /**
     * 禁言/解禁
     *
     * @param userId 用户Id
     */
    @GetMapping("/controlSpeak/{userId}")
    public void controlSpeak(@PathVariable Long userId)
    {
        User user = userService.getUser(userId);
        user.setCanSpeak(!user.getCanSpeak());
        userService.saveUser(user);
    }
}
