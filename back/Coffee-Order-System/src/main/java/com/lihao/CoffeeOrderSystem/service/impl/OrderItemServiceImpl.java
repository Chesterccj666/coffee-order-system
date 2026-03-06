package com.lihao.CoffeeOrderSystem.service.impl;

import com.lihao.CoffeeOrderSystem.service.OrderItemService;
import com.lihao.CoffeeOrderSystem.entity.OrderItem;
import com.lihao.CoffeeOrderSystem.mapper.OrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public boolean batchInsertOrderItems(List<OrderItem> orderItems) {
        int result = orderItemMapper.batchInsert(orderItems);
        return result > 0;
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }
}