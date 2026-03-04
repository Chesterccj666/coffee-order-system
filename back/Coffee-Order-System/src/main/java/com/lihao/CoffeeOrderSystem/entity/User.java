package com.lihao.CoffeeOrderSystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库 user 表
 */
@Data
public class User {
    private Integer id;                 // 主键、自增、唯一索引
    private String username;            // 用户姓名
    private String password;            // 注册/登录密码
    private String phone;               // 手机号（注册/登录凭据）、唯一索引
    private String gender;              // 性别（限制仅"男"or"女"）
    private Integer role;               // 用户角色区分：1.顾客 2.店员 3.管理员
    private LocalDateTime registerTime; // 注册时间
    private String headImage;           // 用户头像url
}
