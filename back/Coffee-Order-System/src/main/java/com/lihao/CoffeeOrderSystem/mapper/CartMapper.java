package com.lihao.CoffeeOrderSystem.mapper;

import com.lihao.CoffeeOrderSystem.entity.Cart;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CartMapper {

    /**
     * 【查看购物车】查询用户的购物车列表
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId}")
    List<Cart> selectByUserId(Integer userId);

    /**
     * 【加入购物车】插入购物车项
     */
    @Insert("INSERT INTO cart(user_id, coffee_id, coffee_name, coffee_image, " +
            "price, quantity, sweet, temperature) " +
            "VALUES(#{userId}, #{coffeeId}, #{coffeeName}, #{coffeeImage}, " +
            "#{price}, #{quantity}, #{sweet}, #{temperature})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Cart cart);

    /**
     * 【修改数量】更新购物车项数量
     */
    @Update("UPDATE cart SET quantity = #{quantity} WHERE id = #{id}")
    int updateQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);

    /**
     * 【删除商品】根据id删除购物车项
     */
    @Delete("DELETE FROM cart WHERE id = #{id}")
    int deleteById(Integer id);

    /**
     * 【判断是否已添加】查询用户购物车中是否已有某咖啡（仅按咖啡ID判断）
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND coffee_id = #{coffeeId}")
    Cart selectByUserAndCoffee(@Param("userId") Integer userId, @Param("coffeeId") Integer coffeeId);
    
    /**
     * 【判断是否已添加】查询用户购物车中是否已有某规格的咖啡（考虑甜度和温度）
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND coffee_id = #{coffeeId} AND sweet = #{sweet} AND temperature = #{temperature}")
    Cart selectByUserAndCoffeeWithSpecs(@Param("userId") Integer userId, @Param("coffeeId") Integer coffeeId, @Param("sweet") Integer sweet, @Param("temperature") Integer temperature);

    /**
     * 【下单后清空】根据用户id清空购物车
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int deleteByUserId(Integer userId);
    
    /**
     * 【根据ID查询】根据id查询购物车项
     */
    @Select("SELECT * FROM cart WHERE id = #{id}")
    Cart selectById(Integer id);
}