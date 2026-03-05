package com.lihao.CoffeeOrderSystem.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 对应数据库 order 表
 */
@Data
public class Order {
    private Integer id;               // 主键、自增
    private String orderNo;           // 订单编号（时间戳+随机数）、唯一索引
    private Integer userId;           // 下单顾客的id（关联user.id）
    private BigDecimal totalAmount;   // 订单总金额
    private Integer status;           // 订单状态：1.待接单 2.制作中 3.已完成 4.已取消
    private String remark;            // 顾客备注
    private String takeCode;          // 取餐码，固定4位，高位不足时补0
    private LocalDateTime orderTime;  // 下单时间、默认当前系统时间
    
    // 用于接收购物车ID列表的字段（不在数据库中）
    private java.util.List<Integer> cartIds;
}