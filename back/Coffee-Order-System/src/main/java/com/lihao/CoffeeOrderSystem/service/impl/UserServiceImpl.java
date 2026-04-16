package com.lihao.CoffeeOrderSystem.service.impl;

import com.lihao.CoffeeOrderSystem.service.UserService;
import com.lihao.CoffeeOrderSystem.entity.User;
import com.lihao.CoffeeOrderSystem.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    @Override
    public boolean registerUser(User user) {
        user.setRegisterTime(LocalDateTime.now());
        int result = userMapper.insert(user);
        return result > 0;
    }

    @Override
    public User getUserById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public boolean updateUser(User user) {
        // 验证手机号是否已被其他用户使用（除了当前用户）
        User existingUser = userMapper.selectByPhone(user.getPhone());
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            return false; // 手机号已被其他用户使用
        }
        
        int result = userMapper.update(user);
        return result > 0;
    }

    @Override
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        // 首先验证原密码是否正确
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false; // 用户不存在
        }
        
        if (!user.getPassword().equals(oldPassword)) {
            return false; // 原密码错误
        }
        
        // 更新密码
        int result = userMapper.updatePassword(user.getId(), newPassword);
        return result > 0;
    }

    @Override
    public User login(String phone, String password) {
        User user = userMapper.selectByPhone(phone);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}