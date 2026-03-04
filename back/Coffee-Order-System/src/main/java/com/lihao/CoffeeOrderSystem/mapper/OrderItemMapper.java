package com.lihao.CoffeeOrderSystem.mapper;

import com.lihao.CoffeeOrderSystem.entity.OrderItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OrderItemMapper {

    /**
     * 【下单】批量插入订单详情（这个方法需要XML实现）
     */
    int batchInsert(List<OrderItem> orderItems);

    /**
     * 【订单详情页】根据订单id查询该订单的所有详情
     */
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> selectByOrderId(Integer orderId);

    /**
     * 【备用】插入单个订单详情
     */
    @Insert("INSERT INTO order_item(order_id, coffee_id, coffee_name, coffee_image, " +
            "price, quantity, total_price, create_time, sweet, temperature) " +
            "VALUES(#{orderId}, #{coffeeId}, #{coffeeName}, #{coffeeImage}, " +
            "#{price}, #{quantity}, #{totalPrice}, #{createTime}, #{sweet}, #{temperature})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItem orderItem);
}