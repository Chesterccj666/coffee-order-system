package com.lihao.CoffeeOrderSystem.service;

import com.lihao.CoffeeOrderSystem.entity.OrderItem;
import java.util.List;

public interface OrderItemService {
    /**
     * 批量插入订单详情
     */
    boolean batchInsertOrderItems(List<OrderItem> orderItems);

    /**
     * 根据订单id查询该订单的所有详情
     */
    List<OrderItem> getOrderItemsByOrderId(Integer orderId);
}