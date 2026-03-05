package com.lihao.CoffeeOrderSystem.Service;

import com.lihao.CoffeeOrderSystem.entity.User;

public interface UserService {
    /**
     * 根据手机号查询用户
     */
    User getUserByPhone(String phone);

    /**
     * 用户注册
     */
    boolean registerUser(User user);

    /**
     * 根据id查询用户
     */
    User getUserById(Integer id);

    /**
     * 更新用户信息
     */
    boolean updateUser(User user);

    /**
     * 用户登录
     */
    User login(String phone, String password);
}