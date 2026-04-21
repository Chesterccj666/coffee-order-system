package com.lihao.CoffeeOrderSystem.mapper;

import com.lihao.CoffeeOrderSystem.entity.Coffee;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

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
        Coffee selectById(@Param("id") Integer id);

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
    @Select("SELECT c.*, SUM(oi.quantity) as totalQuantity FROM coffee c JOIN order_item oi ON c.id = oi.coffee_id JOIN `order` o ON oi.order_id = o.id WHERE o.status = 3 AND o.order_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY c.id ORDER BY totalQuantity DESC LIMIT #{limit}")
    List<Coffee> selectTopSelling(@Param("limit") int limit);
    
    /**
     * 【管理员功能】按类别统计销售数据
     */
    @Select("SELECT c.category, SUM(oi.total_price) as totalSales FROM order_item oi JOIN `order` o ON oi.order_id = o.id JOIN coffee c ON oi.coffee_id = c.id WHERE o.status = 3 GROUP BY c.category")
    List<Map<String, Object>> selectCategorySalesStats();
    
    /**
     * 【管理员功能】查询所有不同的咖啡类别
     */
    @Select("SELECT DISTINCT category FROM coffee WHERE category IS NOT NULL AND category != ''")
    List<String> selectAllCategories();
    
    /**
     * 【管理员功能】查询历史总销售额
     */
    @Select("SELECT COALESCE(SUM(oi.total_price), 0) FROM order_item oi JOIN `order` o ON oi.order_id = o.id WHERE o.status = 3")
    Double selectTotalSalesAmount();
    
    /**
     * 【管理员功能】查询历史总销量
     */
    @Select("SELECT COALESCE(SUM(oi.quantity), 0) FROM order_item oi JOIN `order` o ON oi.order_id = o.id WHERE o.status = 3")
    Integer selectTotalCoffeesSold();
    
    /**
     * 【管理员功能】根据ID更新咖啡图片路径
     */
    @Update("UPDATE coffee SET coffee_image = #{coffeeImage} WHERE id = #{id}")
    int updateImageById(@Param("id") Integer id, @Param("coffeeImage") String coffeeImage);
    
    /**
     * 【首页功能】查询过去七天内热销的咖啡及其销量
     */
    @Select("SELECT c.*, SUM(oi.quantity) as lastWeekSales FROM coffee c JOIN order_item oi ON c.id = oi.coffee_id JOIN `order` o ON oi.order_id = o.id WHERE o.status = 3 AND o.order_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY c.id ORDER BY lastWeekSales DESC LIMIT #{limit}")
    List<Map<String, Object>> selectTopSellingLastWeek(@Param("limit") int limit);
}