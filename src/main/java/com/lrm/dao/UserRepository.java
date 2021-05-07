package com.lrm.dao;

import com.lrm.po.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * JpaRepository是负责分页查找的接口
 *
 * @author 山水夜止
 */
public interface UserRepository extends JpaRepository<User, Long> {

    //注册

    /**
     * 这是Spring Data默认的命名方法的规范 它已经具有了意义 相当于已经被实现了 关键字And 只要符合规范就能解析
     * 通过用户名和密码查找User对象
     *
     * @param username 用户名
     * @param password 密码
     * @return 对应User对象
     */
    User findByUsernameAndPassword(String username, String password);

    /**
     * 通过昵称查找User对象
     *
     * @param nickname 昵称
     * @return 对应User对象
     */
    User findByNickname(String nickname);

    //登录

    /**
     * 通过用户名查找User对象
     *
     * @param username 用户名
     * @return 对应User对象
     */
    User findByUsername(String username);

    /**
     * 按贡献值返回用户
     * 注意一定要有这个 @Query("select q from Question q") ！！不然找不到对应的bean
     *
     * @param pageable 内含分页顺序 其中的size属性与“Top”起限制作用
     * @return 返回前size个 按donation排序
     */
    @Query("select u from User u")
    List<User> findTop(Pageable pageable);

}
