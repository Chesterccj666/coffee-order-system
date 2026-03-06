package com.lihao.CoffeeOrderSystem.service;

import com.lihao.CoffeeOrderSystem.entity.Order;
import com.lihao.CoffeeOrderSystem.entity.OrderItem;
import java.util.List;

public interface OrderService {
    /**
     * 创建订单
     */
    boolean createOrder(Order order, List<Integer> cartIds);

    /**
     * 查询用户的所有订单
     */
    List<Order> getUserOrders(Integer userId);

    /**
     * 根据id查询订单
     */
    Order getOrderById(Integer id);

    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(Integer id, Integer status);

    /**
     * 根据订单号查询订单
     */
    Order getOrderByOrderNo(String orderNo);

    /**
     * 根据状态查询订单（店员使用）
     */
    List<Order> getOrdersByStatus(Integer status);
    
    /**
     * 根据订单ID查询订单项
     */
    List<OrderItem> getOrderItemsByOrderId(Integer orderId);
    
    boolean cancelOrder(Integer orderId);
    
    List<Order> getAllOrders();
    
    /**
     * 更新取餐码
     */
    boolean updateTakeCode(Integer id, String takeCode);
}