package com.lihao.CoffeeOrderSystem.mapper;

import com.lihao.CoffeeOrderSystem.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    /**
     * 【登录/注册查重】根据手机号查询用户
     */
    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User selectByPhone(String phone);

    /**
     * 【注册】插入新用户
     */
    @Insert("INSERT INTO user(username, password, phone, gender, role, register_time, head_image) " +
            "VALUES(#{username}, #{password}, #{phone}, #{gender}, #{role}, #{registerTime}, #{headImage})")
    //@Options这个注解告诉MyBatis在执行INSERT操作后，自动获取数据库生成的主键值，并将其设置回Java对象中。
    //useGeneratedKeys = true 表示开启自动获取主CoffeeOrderSystem/

    //keyProperty = "id" 表示将数据库生成的主键值设置到Java对象的id属性中。
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 【查询个人信息】根据id查询用户
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Integer id);

    /**
     * 【修改个人信息】更新用户信息
     */
    @Update("UPDATE user SET username=#{username}, phone=#{phone}, " +
            "gender=#{gender}, head_image=#{headImage} WHERE id=#{id}")
    int update(User user);
    
    /**
     * 【修改密码】根据id更新用户密码
     */
    @Update("UPDATE user SET password=#{password} WHERE id=#{id}")
    int updatePassword(@Param("id") Integer id, @Param("password") String password);
}