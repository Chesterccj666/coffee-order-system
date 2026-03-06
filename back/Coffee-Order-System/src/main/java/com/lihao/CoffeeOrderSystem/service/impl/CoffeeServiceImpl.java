package com.lihao.CoffeeOrderSystem.service.impl;

import com.lihao.CoffeeOrderSystem.service.CoffeeService;
import com.lihao.CoffeeOrderSystem.entity.Coffee;
import com.lihao.CoffeeOrderSystem.mapper.CoffeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

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
}