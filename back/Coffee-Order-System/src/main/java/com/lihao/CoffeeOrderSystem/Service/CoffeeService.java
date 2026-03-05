package com.lihao.CoffeeOrderSystem.Service;

import com.lihao.CoffeeOrderSystem.entity.Coffee;
import java.util.List;

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
}