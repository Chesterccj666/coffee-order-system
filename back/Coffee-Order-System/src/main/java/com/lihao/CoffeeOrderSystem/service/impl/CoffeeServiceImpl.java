package com.lihao.CoffeeOrderSystem.service.impl;

import com.lihao.CoffeeOrderSystem.service.CoffeeService;
import com.lihao.CoffeeOrderSystem.entity.Coffee;
import com.lihao.CoffeeOrderSystem.mapper.CoffeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class CoffeeServiceImpl implements CoffeeService {

    @Autowired
    private CoffeeMapper coffeeMapper;

    @Override
    public List<Coffee> getAllCoffee() {
        return coffeeMapper.selectAll();
    }

    @Override
    public Coffee getCoffeeById(Integer id) {
        return coffeeMapper.selectById(id);
    }

    @Override
    public List<Coffee> getCoffeeByCategory(String category) {
        return coffeeMapper.selectByCategory(category);
    }

    @Override
    public List<Coffee> getRecommendedCoffee() {
        return coffeeMapper.selectRecommended();
    }
    
    @Override
    public List<Coffee> getAllCoffeeForAdmin() {
        return coffeeMapper.selectAllForAdmin();
    }
    
    @Override
    public boolean addCoffee(Coffee coffee) {
        try {
            // 如果是设置为推荐状态（recommend='1'），则检查当前推荐数量
            if ("1".equals(coffee.getRecommend())) {
                int currentRecommendedCount = coffeeMapper.selectRecommendedCount();
                // 如果当前推荐数量已经达到8个，则不允许再推荐
                if (currentRecommendedCount >= 8) {
                    throw new RuntimeException("推荐的咖啡数量已达上限（8个），无法再推荐更多咖啡");
                }
            }
            coffeeMapper.insert(coffee);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // 如果是推荐数量超限的异常，重新抛出
            if (e.getMessage() != null && e.getMessage().contains("推荐的咖啡数量已达上限")) {
                throw e;
            }
            return false;
        }
    }
    
    @Override
    public boolean updateCoffee(Coffee coffee) {
        try {
            coffeeMapper.updateById(coffee);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteCoffee(Integer id) {
        try {
            coffeeMapper.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean setRecommend(Integer id, String recommend) {
        try {
            // 如果是设置为推荐状态（recommend='1'），则检查当前推荐数量
            if ("1".equals(recommend)) {
                int currentRecommendedCount = coffeeMapper.selectRecommendedCount();
                // 如果当前推荐数量已经达到8个，则不允许再推荐
                if (currentRecommendedCount >= 8) {
                    throw new RuntimeException("推荐的咖啡数量已达上限（8个），无法再推荐更多咖啡");
                }
            }
            coffeeMapper.updateRecommend(id, recommend);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // 如果是推荐数量超限的异常，重新抛出
            if (e.getMessage() != null && e.getMessage().contains("推荐的咖啡数量已达上限")) {
                throw e;
            }
            return false;
        }
    }
    
    @Override
    public boolean setStatus(Integer id, String status) {
        try {
            coffeeMapper.updateStatus(id, status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<Coffee> getTopSellingCoffee(int limit) {
        return coffeeMapper.selectTopSelling(limit);
    }
    
    @Override
    public List<Map<String, Object>> getCategorySalesStats() {
        return coffeeMapper.selectCategorySalesStats();
    }
    
    @Override
    public List<Map<String, Object>> getTopSellingLastWeek(int limit) {
        return coffeeMapper.selectTopSellingLastWeek(limit);
    }
    
    @Override
    public List<String> getAllCategories() {
        return coffeeMapper.selectAllCategories();
    }
    
    @Override
    public Double getTotalSalesAmount() {
        return coffeeMapper.selectTotalSalesAmount();
    }
    
    @Override
    public Integer getTotalCoffeesSold() {
        return coffeeMapper.selectTotalCoffeesSold();
    }
    
    @Override
    public int getRecommendedCount() {
        return coffeeMapper.selectRecommendedCount();
    }
    
    @Override
    public boolean updateCoffeeImage(Integer id, String coffeeImage) {
        try {
            coffeeMapper.updateImageById(id, coffeeImage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}