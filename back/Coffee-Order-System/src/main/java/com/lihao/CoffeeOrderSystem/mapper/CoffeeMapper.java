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
    
    /**
     * 【取消订单】增加库存
     */
    @Update("UPDATE coffee SET stock = stock + #{quantity} WHERE id = #{id}")
    int addStock(@Param("id") Integer id, @Param("quantity") Integer quantity);
    
    /**
     * 【取消订单】减少销量
     */
    @Update("UPDATE coffee SET sales = sales - #{quantity} WHERE id = #{id} AND sales >= #{quantity}")
    int reduceSales(@Param("id") Integer id, @Param("quantity") Integer quantity);
    
    /**
     * 【管理员功能】查询所有咖啡（包括下架的）
     */
    @Select("SELECT * FROM coffee ORDER BY id DESC")
    List<Coffee> selectAllForAdmin();
    
    /**
     * 【管理员功能】插入新的咖啡
     */
    @Insert("INSERT INTO coffee(name, price, coffee_image, description, category, stock, status, recommend, sales) VALUES(#{name}, #{price}, #{coffeeImage}, #{description}, #{category}, #{stock}, #{status}, #{recommend}, #{sales})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Coffee coffee);
    
    /**
     * 【管理员功能】根据ID更新咖啡信息
     */
    @Update("UPDATE coffee SET name=#{name}, price=#{price}, coffee_image=#{coffeeImage}, description=#{description}, category=#{category}, stock=#{stock}, status=#{status}, recommend=#{recommend} WHERE id=#{id}")
    int updateById(Coffee coffee);
    
    /**
     * 【管理员功能】根据ID删除咖啡
     */
    @Delete("DELETE FROM coffee WHERE id = #{id}")
    int deleteById(Integer id);
    
    /**
     * 【管理员功能】更新推荐状态
     */
    @Update("UPDATE coffee SET recommend = #{recommend} WHERE id = #{id}")
    int updateRecommend(@Param("id") Integer id, @Param("recommend") String recommend);
    
    /**
     * 【管理员功能】更新上下架状态
     */
    @Update("UPDATE coffee SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);
    
    /**
     * 【管理员功能】查询销量最高的咖啡
     */
    @Select("SELECT * FROM coffee ORDER BY sales DESC LIMIT #{limit}")
    List<Coffee> selectTopSelling(@Param("limit") int limit);
    
    /**
     * 【管理员功能】按类别统计销售数据
     */
    @Select("SELECT category, SUM(sales) as total_sales, COUNT(*) as count FROM coffee GROUP BY category")
    List<Object> selectCategorySalesStats();
    
    /**
     * 【管理员功能】查询总销售额
     */
    @Select("SELECT SUM(total_amount) FROM `order` WHERE status = 3")
    Double selectTotalSalesAmount();
}