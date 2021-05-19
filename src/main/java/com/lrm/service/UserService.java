package com.lrm.service;

import com.lrm.po.User;

import java.util.List;
import java.util.Map;

/**
 * @author 山水夜止
 */
public interface UserService {

    User checkRegister(String username, String nickname);

    User saveUser(String username, String password, String nickname);

    User saveUser(User user);

    User checkUser(String username, String password);

    User updateUser(User user, Map<String, Object> hashMap);

    User getUser(Long userId);

    User getUser(String nickname);

    Long countUser();

    List<User> listTopUsers(int size);
}
