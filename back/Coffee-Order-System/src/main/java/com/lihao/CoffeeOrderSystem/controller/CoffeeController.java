package com.lihao.CoffeeOrderSystem.controller;

import com.lihao.CoffeeOrderSystem.service.CoffeeService;
import com.lihao.CoffeeOrderSystem.entity.Coffee;
import com.lihao.CoffeeOrderSystem.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coffee")
public class CoffeeController {

    @Autowired
    private CoffeeService coffeeService;

    /**
     * 获取所有上架的咖啡
     */
    @GetMapping("/list")
    public Object getAllCoffee() {
        try {
            List<Coffee> coffeeList = coffeeService.getAllCoffee();
            return new ResponseResult<>(200, "获取成功", coffeeList);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }

    /**
     * 根据ID获取咖啡详情
     */
    @GetMapping("/{id}")
    public Object getCoffeeById(@PathVariable Integer id) {
        try {
            Coffee coffee = coffeeService.getCoffeeById(id);
            if (coffee != null) {
                return new ResponseResult<>(200, "获取成功", coffee);
            } else {
                return new ResponseResult<>(404, "咖啡不存在", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }

    /**
     * 根据分类获取咖啡
     */
    @GetMapping("/category/{category}")
    public Object getCoffeeByCategory(@PathVariable String category) {
        try {
            List<Coffee> coffeeList = coffeeService.getCoffeeByCategory(category);
            return new ResponseResult<>(200, "获取成功", coffeeList);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }

    /**
     * 获取推荐的咖啡
     */
    @GetMapping("/recommended")
    public Object getRecommendedCoffee() {
        try {
            List<Coffee> coffeeList = coffeeService.getRecommendedCoffee();
            return new ResponseResult<>(200, "获取成功", coffeeList);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }
}