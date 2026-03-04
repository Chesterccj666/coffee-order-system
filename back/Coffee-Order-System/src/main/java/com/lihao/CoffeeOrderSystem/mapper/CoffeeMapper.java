package com.lihao.CoffeeOrderSystem.mapper;

import com.lihao.CoffeeOrderSystem.entity.Coffee;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CoffeeMapper {

    /**
     * 【咖啡列表页】查询所有上架的咖啡
     */
    @Select("SELECT * FROM coffee WHERE status = '1'")
    List<Coffee> selectAll();

    /**
     * 【咖啡详情页】根据id查询咖啡
     */
    @Select("SELECT * FROM coffee WHERE id = #{id}")
    Coffee selectById(Integer id);

    /**
     * 【分类筛选】根据分类查询咖啡
     */
    @Select("SELECT * FROM coffee WHERE category = #{category} AND status = '1'")
    List<Coffee> selectByCategory(String category);

    /**
     * 【首页推荐】查询推荐的咖啡
     */
    @Select("SELECT * FROM coffee WHERE recommend = '1' AND status = '1'")
    List<Coffee> selectRecommended();

    /**
     * 【下单扣库存】减少库存（带库存检查）
     */
    @Update("UPDATE coffee SET stock = stock - #{quantity} WHERE id = #{id} AND stock >= #{quantity}")
    //@Param("id") → 将方法参数id映射到SQL中的#{id}， @Param("quantity") → 将方法参数quantity映射到SQL中的#{quantity}。
    int reduceStock(@Param("id") Integer id, @Param("quantity") Integer quantity);

    /**
     * 【下单加销量】增加销量
     */
    @Update("UPDATE coffee SET sales = sales + #{quantity} WHERE id = #{id}")
    int addSales(@Param("id") Integer id, @Param("quantity") Integer quantity);
}