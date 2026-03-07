package com.lihao.CoffeeOrderSystem.service;

import com.lihao.CoffeeOrderSystem.entity.Coffee;
import java.util.List;
import java.util.Map;

public interface CoffeeService {
    /**
     * 查询所有上架的咖啡
     */
    List<Coffee> getAllCoffee();

    /**
     * 根据id查询咖啡
     */
    Coffee getCoffeeById(Integer id);

    /**
     * 根据分类查询咖啡
     */
    List<Coffee> getCoffeeByCategory(String category);

    /**
     * 查询推荐的咖啡
     */
    List<Coffee> getRecommendedCoffee();
    
    /**
     * 管理员功能：获取所有咖啡（包括下架的）
     */
    List<Coffee> getAllCoffeeForAdmin();
    
    /**
     * 管理员功能：添加咖啡
     */
    boolean addCoffee(Coffee coffee);
    
    /**
     * 管理员功能：更新咖啡
     */
    boolean updateCoffee(Coffee coffee);
    
    /**
     * 管理员功能：删除咖啡
     */
    boolean deleteCoffee(Integer id);
    
    /**
     * 管理员功能：设置推荐状态
     */
    boolean setRecommend(Integer id, String recommend);
    
    /**
     * 管理员功能：设置上下架状态
     */
    boolean setStatus(Integer id, String status);
    
    /**
     * 获取销量最高的咖啡（用于管理员首页）
     */
    List<Coffee> getTopSellingCoffee(int limit);
    
    /**
     * 获取按类别统计的销售数据
     */
    List<Map<String, Object>> getCategorySalesStats();
    
    /**
     * 获取所有不同的咖啡类别
     */
    List<String> getAllCategories();
    
    /**
     * 获取总销售额
     */
    Double getTotalSalesAmount();
}