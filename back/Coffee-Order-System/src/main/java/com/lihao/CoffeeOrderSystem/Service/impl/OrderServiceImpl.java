package com.lihao.CoffeeOrderSystem.Service.impl;

import com.lihao.CoffeeOrderSystem.Service.OrderService;
import com.lihao.CoffeeOrderSystem.Service.CartService;
import com.lihao.CoffeeOrderSystem.Service.CoffeeService;
import com.lihao.CoffeeOrderSystem.Service.OrderItemService;
import com.lihao.CoffeeOrderSystem.entity.Order;
import com.lihao.CoffeeOrderSystem.entity.OrderItem;
import com.lihao.CoffeeOrderSystem.entity.Cart;
import com.lihao.CoffeeOrderSystem.entity.Coffee;
import com.lihao.CoffeeOrderSystem.mapper.OrderMapper;
import com.lihao.CoffeeOrderSystem.mapper.CoffeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemService orderItemService;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private CoffeeService coffeeService;
    
    @Autowired
    private CoffeeMapper coffeeMapper;

    @Override
    @Transactional
    public boolean createOrder(Order order, List<Integer> cartIds) {
        try {
            // 从购物车获取商品信息
            List<Cart> cartItems = cartIds.stream()
                    .map(cartId -> cartService.getCartItemById(cartId))
                    .collect(Collectors.toList());

            // 计算订单总金额
            BigDecimal totalAmount = cartItems.stream()
                    .map(cart -> cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 设置订单基本信息
            order.setTotalAmount(totalAmount);
            order.setStatus(1); // 待接单
            order.setOrderTime(LocalDateTime.now());
            order.setOrderNo(generateOrderNo()); // 生成订单号
            order.setTakeCode(generateTakeCode()); // 生成取餐码

            // 插入订单
            int orderResult = orderMapper.insert(order);
            if (orderResult <= 0) {
                return false;
            }

            // 构建订单详情
            List<OrderItem> orderItems = cartItems.stream().map(cart -> {
                OrderItem item = new OrderItem();
                item.setOrderId(order.getId());
                item.setCoffeeId(cart.getCoffeeId());
                item.setCoffeeName(cart.getCoffeeName());
                item.setCoffeeImage(cart.getCoffeeImage());
                item.setPrice(cart.getPrice());
                item.setQuantity(cart.getQuantity());
                item.setTotalPrice(cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
                item.setCreateTime(LocalDateTime.now());
                item.setSweet(cart.getSweet());
                item.setTemperature(cart.getTemperature());
                return item;
            }).collect(Collectors.toList());

            // 批量插入订单详情
            boolean itemsResult = orderItemService.batchInsertOrderItems(orderItems);
            if (!itemsResult) {
                throw new RuntimeException("插入订单详情失败");
            }

            // 扣减库存并增加销量
            for (Cart cart : cartItems) {
                // 检查库存是否充足
                Coffee coffee = coffeeMapper.selectById(cart.getCoffeeId());
                if (coffee.getStock() < cart.getQuantity()) {
                    throw new RuntimeException("库存不足");
                }
                
                // 扣减库存
                int stockResult = coffeeMapper.reduceStock(cart.getCoffeeId(), cart.getQuantity());
                if (stockResult <= 0) {
                    throw new RuntimeException("扣减库存失败");
                }
                
                // 增加销量
                coffeeMapper.addSales(cart.getCoffeeId(), cart.getQuantity());
            }

            // 清空购物车
            for (Integer cartId : cartIds) {
                cartService.deleteCartItem(cartId);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("创建订单失败", e);
        }
    }

    @Override
    public List<Order> getUserOrders(Integer userId) {
        return orderMapper.selectByUserId(userId);
    }

    @Override
    public Order getOrderById(Integer id) {
        return orderMapper.selectById(id);
    }

    @Override
    public boolean updateOrderStatus(Integer id, Integer status) {
        int result = orderMapper.updateStatus(id, status);
        return result > 0;
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public List<Order> getOrdersByStatus(Integer status) {
        return orderMapper.selectByStatus(status);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        return orderItemService.getOrderItemsByOrderId(orderId);
    }
    
    // 生成订单号（时间戳+随机数）
    private String generateOrderNo() {
        long timestamp = System.currentTimeMillis();
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000; // 生成4位随机数
        return String.valueOf(timestamp) + randomNum;
    }
    
    // 生成取餐码（4位数字，高位不足时补0）
    private String generateTakeCode() {
        Random random = new Random();
        int code = random.nextInt(10000); // 0-9999
        return String.format("%04d", code);
    }
}