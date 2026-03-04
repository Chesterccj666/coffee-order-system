package com.lihao.CoffeeOrderSystem.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 咖啡实体类
 * 对应数据库 coffee 表
 */
@Data
public class Coffee {
    private Integer id;              // 主键、自增、唯一索引
    private String name;             // 咖啡名称
    private BigDecimal price;        // 单杯价格（默认中杯）（保留两位小数）
    private String coffeeImage;      // 咖啡图片url
    private String description;      // 咖啡描述（口味介绍等内容）
    private String category;         // 咖啡所属种类（美式、意式等）
    private Integer stock;           // 库存数量、默认值为0
    private String status;           // 上架状态：1.上架 2.下架
    private String recommend;        // 是否推荐：1.推荐 2.不推荐
    private Integer sales;           // 已销售的数量、默认值为0
}
