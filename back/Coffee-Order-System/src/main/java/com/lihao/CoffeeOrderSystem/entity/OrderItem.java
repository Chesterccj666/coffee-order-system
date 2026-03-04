package com.lihao.CoffeeOrderSystem.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单详情实体类
 * 对应数据库 order_item 表
 */
@Data
public class OrderItem {
    private Integer id;               // 主键、自增
    private Integer orderId;          // 所属订单id（关联order.id）
    private Integer coffeeId;         // 所含咖啡id（关联coffee.id）
    private String coffeeName;        // 咖啡名称（快照）
    private String coffeeImage;       // 咖啡图片url（快照）
    private BigDecimal price;         // 单价（快照）
    private Integer quantity;         // 购买数量
    private BigDecimal totalPrice;    // 小计金额（price * quantity）
    private LocalDateTime createTime; // 订单创建时间、默认当前系统时间
    private Integer sweet;            // 甜度：1：正常糖 2：少糖 3：不加糖
    private Integer temperature;      // 温度：1：烫 2：温热 3：少冰 4：正常冰
}
