package com.lihao.CoffeeOrderSystem.controller;

import com.lihao.CoffeeOrderSystem.service.OrderService;
import com.lihao.CoffeeOrderSystem.entity.Order;
import com.lihao.CoffeeOrderSystem.entity.OrderItem;
import com.lihao.CoffeeOrderSystem.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Object createOrder(@RequestBody Order order) {
        try {
            // 这里假设购物车ID列表通过请求体传递
            // 在实际应用中，可能需要调整参数结构
            boolean success = orderService.createOrder(order, order.getCartIds());
            if (success) {
                return new ResponseResult<>(200, "订单创建成功", order);
            } else {
                return new ResponseResult<>(500, "订单创建失败", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "订单创建失败：" + e.getMessage(), null);
        }
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/list/{userId}")
    public Object getUserOrders(@PathVariable Integer userId) {
        try {
            List<Order> orders = orderService.getUserOrders(userId);
            return new ResponseResult<>(200, "获取成功", orders);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Object getOrderById(@PathVariable Integer id) {
        try {
            Order order = orderService.getOrderById(id);
            if (order != null) {
                return new ResponseResult<>(200, "获取成功", order);
            } else {
                return new ResponseResult<>(404, "订单不存在", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/updateStatus")
    public Object updateOrderStatus(@RequestParam Integer id, @RequestParam Integer status) {
        try {
            boolean success = orderService.updateOrderStatus(id, status);
            if (success) {
                return new ResponseResult<>(200, "更新成功", null);
            } else {
                return new ResponseResult<>(500, "更新失败", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "更新失败：" + e.getMessage(), null);
        }
    }

    /**
     * 根据订单号获取订单
     */
    @GetMapping("/byOrderNo/{orderNo}")
    public Object getOrderByOrderNo(@PathVariable String orderNo) {
        try {
            Order order = orderService.getOrderByOrderNo(orderNo);
            if (order != null) {
                return new ResponseResult<>(200, "获取成功", order);
            } else {
                return new ResponseResult<>(404, "订单不存在", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }

    /**
     * 根据状态获取订单列表（供店员使用）
     */
    @GetMapping("/byStatus/{status}")
    public Object getOrdersByStatus(@PathVariable Integer status) {
        try {
            List<Order> orders = orderService.getOrdersByStatus(status);
            return new ResponseResult<>(200, "获取成功", orders);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }
    
    /**
     * 获取订单详情（包含订单项）
     */
    @GetMapping("/detail/{id}")
    public Object getOrderDetail(@PathVariable Integer id) {
        try {
            Order order = orderService.getOrderById(id);
            if (order != null) {
                // 获取订单项
                List<OrderItem> orderItems = orderService.getOrderItemsByOrderId(id);
                
                // 将订单项添加到订单对象中
                Map<String, Object> result = new HashMap<>();
                result.put("order", order);
                result.put("items", orderItems);
                
                return new ResponseResult<>(200, "获取成功", result);
            } else {
                return new ResponseResult<>(404, "订单不存在", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }
    
    /**
     * 取消订单
     */
    @PutMapping("/cancel/{orderId}")
    public Object cancelOrder(@PathVariable Integer orderId) {
        try {
            boolean success = orderService.cancelOrder(orderId);
            if (success) {
                return new ResponseResult<>(200, "订单取消成功", null);
            } else {
                return new ResponseResult<>(500, "订单取消失败", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "订单取消失败：" + e.getMessage(), null);
        }
    }
}