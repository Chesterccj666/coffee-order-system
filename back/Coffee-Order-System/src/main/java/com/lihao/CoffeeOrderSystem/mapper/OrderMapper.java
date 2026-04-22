package com.lihao.CoffeeOrderSystem.mapper;

import com.lihao.CoffeeOrderSystem.entity.Order;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

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
     * 【更新取餐码】在订单完成时设置取餐码
     */
    @Update("UPDATE `order` SET take_code = #{takeCode} WHERE id = #{id}")
    int updateTakeCode(@Param("id") Integer id, @Param("takeCode") String takeCode);

    /**
     * 【根据订单号查询】备用
     */
    @Select("SELECT * FROM `order` WHERE order_no = #{orderNo}")
    Order selectByOrderNo(String orderNo);

    /**
     * 【店员筛选】根据状态查询订单
     */
    @Select("SELECT * FROM `order` WHERE status = #{status} ORDER BY order_time DESC")
    List<Order> selectByStatus(Integer status);
    
    /**
     * 【顾客筛选】根据用户ID和状态查询订单
     */
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} AND status = #{status} ORDER BY order_time DESC")
    List<Order> selectByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") Integer status);
    
    /**
     * 【店员功能】查询所有订单
     */
    @Select("SELECT * FROM `order` ORDER BY order_time DESC")
    List<Order> selectAllOrders();
    
    /**
     * 【管理员功能】获取过去七天的每日销售额
     */
    @Select("SELECT DATE(order_time) as date, COALESCE(SUM(total_amount), 0) as sales FROM `order` WHERE status = 3 AND order_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY DATE(order_time) ORDER BY DATE(order_time)")
    List<Map<String, Object>> selectDailySalesForLastWeek();
    
    /**
     * 【管理员功能】获取过去七天的每日销量
     */
    @Select("SELECT DATE(o.order_time) as date, COALESCE(SUM(oi.quantity), 0) as quantity FROM `order` o JOIN order_item oi ON o.id = oi.order_id WHERE o.status = 3 AND o.order_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY DATE(o.order_time) ORDER BY DATE(o.order_time)")
    List<Map<String, Object>> selectDailyQuantityForLastWeek();
}