package com.lrm.web;

import com.lrm.po.User;
import com.lrm.service.UserService;
import com.lrm.util.MyBeanUtils;
import com.lrm.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author 山水夜止
 */
@RestController
public class RankController {
    @Autowired
    UserService userService;

    /**
     * @return 按贡献值排序的用户集合 选择前十
     */
    @GetMapping("/rank")
    Result<Map<String, Object>> donationRank() {
        Map<String, Object> hashMap = new HashMap<>(1);

        //返回十个贡献度最高的用户
        List<User> users = userService.listTopUsers(10);

        //真正要返回的数据
        List<User> newUsers = new ArrayList<>(10);
        for (User user : users) {
            //规范需要返回啥数据
            User model = new User();
            model.setDonation(user.getDonation());
            model.setNickname(user.getNickname());
            model.setAvatar(user.getAvatar());

            //这里以后要重写copyProperties 把空的关联对象也忽略
//            String[] ignores = MyBeanUtils.getNullPropertyNames(model);
//            BeanUtils.copyProperties(user, model, ignores);
            newUsers.add(model);
        }
        hashMap.put("users", newUsers);

        return new Result<>(hashMap, null);
    }
}
