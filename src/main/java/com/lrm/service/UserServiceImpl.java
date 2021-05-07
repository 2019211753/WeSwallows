package com.lrm.service;

import com.lrm.dao.UserRepository;
import com.lrm.po.Tag;
import com.lrm.po.User;
import com.lrm.util.MD5Utils;
import com.lrm.util.MyBeanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author 山水夜止
 */
@Service
public class UserServiceImpl implements UserService
{
    /**
     *  依赖注入 在某类中应用其他类的方法 需要调用这个类的对象 这就是依赖
     */
    @Autowired
    private UserRepository userRepository;

    //验证该用户是否已经注册
    //注册了的不能再注册（用户名或昵称是否已经存在）
    //注册了的可以直接登录

    /**
     * 检查
     */
    @Override
    public User checkRegister(String username, String nickname) {
        User user1 = userRepository.findByUsername(username);
        User user2 = userRepository.findByNickname(nickname);
        //不返回就是返回null
        if(user1 != null)
        {
            return user1;
        } else {
            return user2;
        }
    }


    /**
     * 保存到数据库
     *
     * @param username 账号
     * @param password 密码
     * @param nickname 用户名
     */
    @Override
    @Transactional
    public User saveUser(String username, String password, String nickname) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("M#D5+" + MD5Utils.code(password));
        user.setNickname(nickname);
        //默认头像
        user.setAvatar("images/3.jpg");
        user.setDonation(0);
        user.setCanSpeak(true);
        user.setAdmin(false);
        user.setRegisterTime(new Date());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 登录
     */
    @Override
    public User checkUser(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, "M#D5+" + MD5Utils.code(password));
    }

    /**
     * 更新用户
     */
    @Override
    @Transactional
    public User updateUser(User user) {
        //这个u和user虽然id相同，但是已经不是一个对象了！！！user里有新内容，但不包含 前端隐含域不包括的内容。u没有新内容，但是有全部的内容。
        // 所以说要不要这么做 主要看前端有没有隐含域
        User u = getUser(user.getId());
        //user赋值给u
        BeanUtils.copyProperties(user, u, MyBeanUtils.getNullPropertyNames(user));
        return userRepository.save(user);
    }


    @Override
    public User getUser(Long userId) {
        Optional<User> opUser = userRepository.findById(userId);
        return opUser.orElse(null);
    }

    @Override
    public User getUser(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    @Override
    public Long countUser() {
        return userRepository.count();
    }

    @Override
    public List<User> listTopUsers(int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "donation");
        Pageable pageable = PageRequest.of(0, size, sort);
        return userRepository.findTop(pageable);
    }
}
