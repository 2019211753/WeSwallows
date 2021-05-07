package com.lrm.web.customer;

import com.lrm.po.User;
import com.lrm.service.UserService;
import com.lrm.util.FileUtils;
import com.lrm.util.GetTokenInfo;
import com.lrm.vo.Magic;
import com.lrm.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 山水夜止
 */
@RequestMapping("/customer")
@RestController
public class CustomerController {
    @Autowired
    private UserService userService;

    @Value("${web.upload-path}")
    private String path;


    /**
     * 返回个人信息
     * 获取当前用户id
     *
     * @param request 获取token
     * @return user: 当前用户对象
     */
    @GetMapping("/personal")
    public Result<Map<String, Object>> showMe(HttpServletRequest request) {
        Map<String, Object> hashMap = new HashMap<>(2);

        User user = new User();
        BeanUtils.copyProperties(userService.getUser(GetTokenInfo.getCustomUserId(request)), user);

        //返回当前用户信息和院系选择
        hashMap.put("user", user);
        hashMap.put("ACADEMIES", Magic.ACADEMIES);

        return new Result<>(hashMap, true, "");
    }

    //下面两个资料修改最好分开

    /**
     * 上传头像到本地 获取path返回
     *
     * @param req  获取当前用户id
     * @param file 被上传的文件
     * @return avatar 文件在服务器端的路径
     */
    @PostMapping("/uploadAvatar")
    public Result<Map<String, Object>> uploadAvatar(MultipartFile file, HttpServletRequest req) {
        Map<String, Object> hashMap = new HashMap<>(1);

        Long userId = GetTokenInfo.getCustomUserId(req);

        //创建存放文件的文件夹的流程

        //头像文件夹的绝对路径
        String realPath = path + "/" + userId + "/avatar";


        //所上传的文件原名
        String oldName = file.getOriginalFilename();

        //保存文件到文件夹中 获得新文件名
        String newName = FileUtils.upload(file, realPath, oldName);

        if (newName != null) {
            User user = userService.getUser(userId);
            user.setAvatar("images/" + userId + "/avatar/" + newName);

            userService.saveUser(user);

            return new Result<>(hashMap, true, "上传成功");
        } else {
            return new Result<>(hashMap, false, "上传失败");
        }
    }

    /**
     * 修改所有信息 封装成User返回
     *
     * @param user 旧用户对象
     * @return user：新用户对象
     */
    @PostMapping("/modifyAll")
    public Result<Map<String, Object>> modifyUserInformation(User user)
    {
        Map<String, Object> hashMap = new HashMap<>(1);

        hashMap.put("user", userService.updateUser(user));

        return new Result<>(hashMap, true, "修改成功");
    }




}
