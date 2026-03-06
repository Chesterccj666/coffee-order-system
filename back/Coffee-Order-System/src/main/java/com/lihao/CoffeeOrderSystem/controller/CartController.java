package com.lihao.CoffeeOrderSystem.controller;

import com.lihao.CoffeeOrderSystem.service.CartService;
import com.lihao.CoffeeOrderSystem.entity.Cart;
import com.lihao.CoffeeOrderSystem.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 获取用户购物车列表
     */
    @GetMapping("/list/{userId}")
    public Object getUserCart(@PathVariable Integer userId) {
        try {
            List<Cart> cartList = cartService.getUserCart(userId);
            return new ResponseResult<>(200, "获取成功", cartList);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public Object addToCart(@RequestBody Cart cart) {
        try {
            boolean success = cartService.addToCart(cart);
            if (success) {
                return new ResponseResult<>(200, "添加成功", null);
            } else {
                return new ResponseResult<>(500, "添加失败", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "添加失败：" + e.getMessage(), null);
        }
    }

    /**
     * 更新购物车项数量
     */
    @PutMapping("/updateQuantity")
    public Object updateCartItemQuantity(@RequestParam Integer id, @RequestParam Integer quantity) {
        try {
            boolean success = cartService.updateCartItemQuantity(id, quantity);
            if (success) {
                return new ResponseResult<>(200, "更新成功", null);
            } else {
                return new ResponseResult<>(500, "更新失败", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "更新失败：" + e.getMessage(), null);
        }
    }

    /**
     * 删除购物车项
     */
    @DeleteMapping("/delete/{id}")
    public Object deleteCartItem(@PathVariable Integer id) {
        try {
            boolean success = cartService.deleteCartItem(id);
            if (success) {
                return new ResponseResult<>(200, "删除成功", null);
            } else {
                return new ResponseResult<>(500, "删除失败", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "删除失败：" + e.getMessage(), null);
        }
    }
}