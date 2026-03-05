package com.lihao.CoffeeOrderSystem.Service.impl;

import com.lihao.CoffeeOrderSystem.Service.CartService;
import com.lihao.CoffeeOrderSystem.entity.Cart;
import com.lihao.CoffeeOrderSystem.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public List<Cart> getUserCart(Integer userId) {
        return cartMapper.selectByUserId(userId);
    }

    @Override
    public boolean addToCart(Cart cart) {
        // 检查是否已存在相同的商品（相同用户、相同咖啡、相同规格）
        Cart existingCart = cartMapper.selectByUserAndCoffee(cart.getUserId(), cart.getCoffeeId());
        if (existingCart != null) {
            // 如果已存在，则更新数量
            int newQuantity = existingCart.getQuantity() + cart.getQuantity();
            int result = cartMapper.updateQuantity(existingCart.getId(), newQuantity);
            return result > 0;
        } else {
            // 如果不存在，则插入新的购物车项
            int result = cartMapper.insert(cart);
            return result > 0;
        }
    }

    @Override
    public boolean updateCartItemQuantity(Integer id, Integer quantity) {
        int result = cartMapper.updateQuantity(id, quantity);
        return result > 0;
    }

    @Override
    public boolean deleteCartItem(Integer id) {
        int result = cartMapper.deleteById(id);
        return result > 0;
    }

    @Override
    public Cart getCartItemByUserAndCoffee(Integer userId, Integer coffeeId) {
        return cartMapper.selectByUserAndCoffee(userId, coffeeId);
    }

    @Override
    public boolean clearCartByUserId(Integer userId) {
        int result = cartMapper.deleteByUserId(userId);
        return result > 0;
    }

    @Override
    public Cart getCartItemById(Integer id) {
        return cartMapper.selectById(id);
    }
}