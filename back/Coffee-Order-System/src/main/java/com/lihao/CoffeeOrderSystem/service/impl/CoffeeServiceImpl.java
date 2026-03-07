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
            coffeeMapper.insert(coffee);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
            coffeeMapper.updateRecommend(id, recommend);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
    public List<String> getAllCategories() {
        return coffeeMapper.selectAllCategories();
    }
    
    @Override
    public Double getTotalSalesAmount() {
        return coffeeMapper.selectTotalSalesAmount();
    }
}