package com.lihao.CoffeeOrderSystem.Service;

import com.lihao.CoffeeOrderSystem.entity.Cart;
import java.util.List;

public interface CartService {
    /**
     * 查询用户的购物车列表
     */
    List<Cart> getUserCart(Integer userId);

    /**
     * 添加商品到购物车
     */
    boolean addToCart(Cart cart);

    /**
     * 更新购物车项数量
     */
    boolean updateCartItemQuantity(Integer id, Integer quantity);

    /**
     * 删除购物车项
     */
    boolean deleteCartItem(Integer id);

    /**
     * 根据用户ID和咖啡ID查询购物车项
     */
    Cart getCartItemByUserAndCoffee(Integer userId, Integer coffeeId);

    /**
     * 根据用户ID清空购物车
     */
    boolean clearCartByUserId(Integer userId);
    
    /**
     * 根据ID查询购物车项
     */
    Cart getCartItemById(Integer id);
}