package com.lrm.web.customer;

import com.lrm.Exception.NormalException;
import com.lrm.po.User;
import com.lrm.service.UserService;
import com.lrm.util.*;
import com.lrm.vo.Magic;
import com.lrm.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 山水夜止
 */
@RequestMapping("/customer")
@RestController
public class ProfileController {
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
        //这里返回的content里的base64编码可能耗内存 可以考虑优化 在后端存储图片路径
        Map<String, Object> hashMap = new HashMap<>(2);

        User user = new User();
        BeanUtils.copyProperties(userService.getUser(TokenInfo.getCustomUserId(request)), user);

        //返回当前用户信息和院系选择
        hashMap.put("user", user);
        hashMap.put("ACADEMIES", Magic.ACADEMIES);

        return new Result<>(hashMap, "");
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
    public Result<Map<String, Object>> uploadAvatar(MultipartFile file, HttpServletRequest req) throws IOException {
        Map<String, Object> hashMap = new HashMap<>(1);

        Long userId = TokenInfo.getCustomUserId(req);

        //创建存放文件的文件夹的流程

        //头像文件夹的绝对路径
        String realPath = path + "/" + userId + "/avatar";

        //所上传的文件原名
        String oldName = file.getOriginalFilename();

        //保存文件到文件夹中 获得新文件名
        FileUtils.rebuildFolder(realPath);
        String newName = FileUtils.upload(file, realPath, oldName);

        if (newName != null) {
            User user = userService.getUser(userId);
            user.setAvatar("images/" + userId + "/avatar/" + newName);

            userService.saveUser(user);

            return new Result<>(hashMap, "上传成功");
        } else {
            throw new NormalException("文件过大，上传失败");
        }
    }

    /**
     * 修改发送过来的信息
     * 最后检查昵称是否已经存在，存在的话就改其他的，返回前端昵称已存在。
     * 最后检查密码是否符合格式规范。
     */
    @PostMapping("/modifyAll")
    public Result<Map<String, Object>> modifyUserInformation(HttpServletRequest request, String nickname, String password, String email,
                                                             String qqId, String wechatId, Boolean sex, String personalSignature, String academy,
                                                             String major) {
        Map<String, Object> hashMap = new HashMap<>(2);

        StringBuilder errorMessage = null;

        //获得当前用户Id 检查用户需要更改的昵称有没用其他用户在使用
        Long customerUserId = TokenInfo.getCustomUserId(request);
        User user0 = userService.getUser(nickname);

        User user = new User();
        user.setId(customerUserId);

        //填进去
        if (!"".equals(email) && email != null) {
            user.setEmail(email);
        }

        if (!"".equals(qqId) && qqId != null) {
            user.setQQId(qqId);
        }

        if (!"".equals(wechatId) && wechatId != null) {
            user.setWechatId(wechatId);
        }

        user.setSex(sex);

        if (!"".equals(personalSignature) && personalSignature != null) {
            user.setPersonalSignature(personalSignature);
        }

        if (!"".equals(academy) && academy != null) {
            user.setAcademy(academy);
        }

        if (!"".equals(major) && major != null) {
            user.setMajor(major);
        }

        if (!"".equals(password) && password != null) {
            if (StringVerify.isContainChinese(password) && (password.length() > 12 || password.length() < 7)) {
                user.setPassword("M#D5+" + MD5Utils.code(password));
            } else {
                errorMessage = new StringBuilder("密码格式错误；");
            }
        }

        if (!(user0 != null && user0.getId().equals(customerUserId))) {
            user.setNickname(nickname);
            userService.updateUser(user, hashMap);
        } else {
            userService.updateUser(user, hashMap);

            if (errorMessage == null) {
                errorMessage = new StringBuilder("昵称已被占用；");
            } else {
                errorMessage.append("昵称已被占用；");
            }
        }

        if (errorMessage != null) {
            throw new NormalException(errorMessage.append("其他信息修改成功；").toString());
        }

        return new Result<>(hashMap, "修改成功");
    }




}
