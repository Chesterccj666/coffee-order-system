package com.lihao.CoffeeOrderSystem.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 购物车实体类
 * 对应数据库 cart 表
 */
@Data
public class Cart {
    private Integer id;              // 主键、自增
    private Integer userId;          // 顾客id（关联user.id）
    private Integer coffeeId;        // 咖啡id（关联coffee.id）
    private String coffeeName;       // 咖啡名称（冗余存储，便于显示）
    private String coffeeImage;      // 咖啡图片url（冗余存储）
    private BigDecimal price;        // 单价（顾客下单时的价格）
    private Integer quantity;        // 加购的数量
    private Integer sweet;           // 甜度（冗余存储）：1：正常糖 2：少糖 3：不加糖
    private Integer temperature;     // 温度（冗余存储）：1：烫 2：温热 3：少冰 4：正常冰
}