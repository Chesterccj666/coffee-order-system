package com.lihao.CoffeeOrderSystem.mapper;

import com.lihao.CoffeeOrderSystem.entity.Order;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 【创建订单】插入新订单
     */
    @Insert("INSERT INTO `order`(order_no, user_id, total_amount, status, remark, take_code, order_time) " +
            "VALUES(#{orderNo}, #{userId}, #{totalAmount}, #{status}, #{remark}, #{takeCode}, #{orderTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    /**
     * 【我的订单】查询用户的所有订单
     */
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY order_time DESC")
    List<Order> selectByUserId(Integer userId);

    /**
     * 【订单详情】根据id查询订单
     */
    @Select("SELECT * FROM `order` WHERE id = #{id}")
    Order selectById(Integer id);

    /**
     * 【更新状态】接单、完成、取消
     */
    @Update("UPDATE `order` SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 【根据订单号查询】备用
     */
    @Select("SELECT * FROM `order` WHERE order_no = #{orderNo}")
    Order selectByOrderNo(String orderNo);

    /**
     * 【店员筛选】根据状态查询订单
     */
    @Select("SELECT * FROM `order` WHERE status = #{status} ORDER BY order_time")
    List<Order> selectByStatus(Integer status);
}