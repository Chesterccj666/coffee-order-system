package com.lihao.CoffeeOrderSystem.controller;

import com.lihao.CoffeeOrderSystem.service.CoffeeService;
import com.lihao.CoffeeOrderSystem.service.OrderService;
import com.lihao.CoffeeOrderSystem.entity.Coffee;
import com.lihao.CoffeeOrderSystem.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coffee")
public class CoffeeController {

    @Autowired
    private CoffeeService coffeeService;
    
    @Autowired
    private OrderService orderService;

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

    /**
     * 管理员功能：获取所有咖啡（包括下架的）
     */
    @GetMapping("/admin/list")
    public Object getAllCoffeeForAdmin() {
        try {
            List<Coffee> coffeeList = coffeeService.getAllCoffeeForAdmin();
            return new ResponseResult<>(200, "获取成功", coffeeList);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }

    /**
     * 管理员功能：添加咖啡
     */
    @PostMapping("/admin/add")
    public Object addCoffee(
            @RequestParam("name") String name,
            @RequestParam("price") String price,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("stock") Integer stock,
            @RequestParam("status") String status,
            @RequestParam("recommend") String recommend,
            @RequestParam("image") MultipartFile image) {
        Coffee coffee = null;
        File tempFile = null;
        try {
            // 首先处理图片，生成临时图片文件
            String tempFileName = "temp_coffee_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000) + ".jpg"; // 使用临时文件名
            // 使用与WebMvcConfig一致的上传路径
            String baseUploadPath = System.getProperty("user.home") + "/CoffeeOrderSystem/upload/";
            String uploadDir = baseUploadPath + "coffee_image/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            tempFile = new File(uploadPath, tempFileName);
            
            // 将上传的图片转换为JPG格式并保存
            BufferedImage bufferedImage = javax.imageio.ImageIO.read(image.getInputStream());
            javax.imageio.ImageIO.write(bufferedImage, "jpg", tempFile);
            
            // 创建咖啡对象并设置基本信息（暂时使用临时图片路径）
            coffee = new Coffee();
            coffee.setName(name);
            coffee.setPrice(java.math.BigDecimal.valueOf(Double.parseDouble(price)));
            coffee.setCoffeeImage("upload/coffee_image/" + tempFileName); // 临时图片路径
            coffee.setDescription(description);
            coffee.setCategory(category);
            coffee.setStock(stock);
            coffee.setStatus(status);
            coffee.setRecommend(recommend);
            coffee.setSales(0); // 新增咖啡销量为0
            
            // 保存咖啡信息（此时包含临时图片路径）
            boolean success = coffeeService.addCoffee(coffee);
            if (!success) {
                // 如果数据库插入失败，删除已保存的临时图片
                tempFile.delete();
                return new ResponseResult<>(500, "添加失败", null);
            }
            
            // 成功保存后，将临时图片重命名为正确格式：coffee-{ID}.jpg
            String finalFileName = "coffee-" + coffee.getId() + ".jpg";
            File finalFile = new File(uploadPath, finalFileName);
            
            // 重命名文件
            if (tempFile.renameTo(finalFile)) {
                // 更新数据库中的图片路径
                coffee.setCoffeeImage("upload/coffee_image/" + finalFileName);
                coffeeService.updateCoffeeImage(coffee.getId(), coffee.getCoffeeImage());
            } else {
                // 如果重命名失败，至少保留临时文件名
                // 但这种情况应该很少见
                System.out.println("警告：无法重命名图片文件，保留临时文件名");
            }
            
            return new ResponseResult<>(200, "添加成功", coffee);
        } catch (IOException e) {
            return new ResponseResult<>(500, "图片上传失败：" + e.getMessage(), null);
        } catch (IllegalArgumentException e) {
            return new ResponseResult<>(500, "图片格式不支持：" + e.getMessage(), null);
        } catch (Exception e) {
            // 如果出现其他异常，尝试删除临时图片（如果有的话）
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
            // 如果coffee对象已创建且数据库中有记录，尝试清理
            if (coffee != null && coffee.getId() != null) {
                try {
                    // 删除数据库记录
                    coffeeService.deleteCoffee(coffee.getId());
                } catch (Exception ex) {
                    // 删除数据库记录失败，不影响主流程
                }
            }
            return new ResponseResult<>(500, "添加失败：" + e.getMessage(), null);
        }
    }

    /**
     * 管理员功能：更新咖啡
     */
    @PutMapping("/admin/update")
    public Object updateCoffee(
            @RequestParam("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam("price") String price,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("stock") Integer stock,
            @RequestParam("status") String status,
            @RequestParam("recommend") String recommend,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Coffee existingCoffee = coffeeService.getCoffeeById(id);
            if (existingCoffee == null) {
                return new ResponseResult<>(404, "咖啡不存在", null);
            }
            
            // 如果提供了新图片，则替换原图片
            String imagePath = existingCoffee.getCoffeeImage(); // 默认保持原有图片路径
            if (image != null && !image.isEmpty()) {
                // 删除旧图片
                if (existingCoffee.getCoffeeImage() != null && !existingCoffee.getCoffeeImage().isEmpty()) {
                    // 构建正确的文件路径
                    String baseUploadPath = System.getProperty("user.home") + "/CoffeeOrderSystem/upload/";
                    String oldImagePath = existingCoffee.getCoffeeImage();
                    // 如果路径以"upload/"开头，构造完整的物理路径
                    if (oldImagePath.startsWith("upload/")) {
                        oldImagePath = baseUploadPath + oldImagePath.substring("upload/".length());
                    }
                    File oldFile = new File(oldImagePath);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
                
                // 保存新图片，使用"coffee-此咖啡的主键id"格式作为文件名
                String fileName = "coffee-" + id + ".jpg"; // 使用"coffee-此咖啡的主键id"格式，统一为jpg扩展名
                
                // 使用与WebMvcConfig一致的上传路径
                String baseUploadPath = System.getProperty("user.home") + "/CoffeeOrderSystem/upload/";
                String uploadDir = baseUploadPath + "coffee_image/";
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();
                }
                File file = new File(uploadPath, fileName);
                
                // 将上传的图片转换为JPG格式并保存
                BufferedImage bufferedImage = javax.imageio.ImageIO.read(image.getInputStream());
                javax.imageio.ImageIO.write(bufferedImage, "jpg", file);
                
                imagePath = "upload/coffee_image/" + fileName;
            }
            
            Coffee coffee = new Coffee();
            coffee.setId(id);
            coffee.setName(name);
            coffee.setPrice(java.math.BigDecimal.valueOf(Double.parseDouble(price)));
            coffee.setCoffeeImage(imagePath);
            coffee.setDescription(description);
            coffee.setCategory(category);
            coffee.setStock(stock);
            coffee.setStatus(status);
            coffee.setRecommend(recommend);
            // 保持原有的销量数据
            coffee.setSales(existingCoffee.getSales());
            
            boolean success = coffeeService.updateCoffee(coffee);
            if (success) {
                return new ResponseResult<>(200, "更新成功", coffee);
            } else {
                return new ResponseResult<>(500, "更新失败", null);
            }
        } catch (IOException e) {
            return new ResponseResult<>(500, "图片处理失败：" + e.getMessage(), null);
        } catch (IllegalArgumentException e) {
            return new ResponseResult<>(500, "图片格式不支持：" + e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseResult<>(500, "更新失败：" + e.getMessage(), null);
        }
    }

    /**
     * 管理员功能：删除咖啡
     */
    @DeleteMapping("/admin/delete/{id}")
    public Object deleteCoffee(@PathVariable Integer id) {
        try {
            Coffee coffee = coffeeService.getCoffeeById(id);
            if (coffee == null) {
                return new ResponseResult<>(404, "咖啡不存在", null);
            }
            
            // 删除咖啡图片文件
            if (coffee.getCoffeeImage() != null && !coffee.getCoffeeImage().isEmpty()) {
                // 处理图片路径，确保正确构建文件路径
                String imagePath = coffee.getCoffeeImage();
                // 如果路径以"/"开头，去掉开头的"/"以避免路径问题
                if (imagePath.startsWith("/")) {
                    imagePath = imagePath.substring(1);
                }
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
            
            boolean success = coffeeService.deleteCoffee(id);
            if (success) {
                return new ResponseResult<>(200, "删除成功", null);
            } else {
                return new ResponseResult<>(500, "删除失败", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "删除失败：" + e.getMessage(), null);
        }
    }

    /**
     * 管理员功能：设置推荐状态
     */
    @PutMapping("/admin/set-recommend/{id}/{recommend}")
    public Object setRecommend(@PathVariable Integer id, @PathVariable String recommend) {
        try {
            boolean success = coffeeService.setRecommend(id, recommend);
            if (success) {
                return new ResponseResult<>(200, "设置成功", null);
            } else {
                return new ResponseResult<>(500, "设置失败", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "设置失败：" + e.getMessage(), null);
        }
    }

    /**
     * 管理员功能：设置上下架状态
     */
    @PutMapping("/admin/set-status/{id}/{status}")
    public Object setStatus(@PathVariable Integer id, @PathVariable String status) {
        try {
            boolean success = coffeeService.setStatus(id, status);
            if (success) {
                return new ResponseResult<>(200, "设置成功", null);
            } else {
                return new ResponseResult<>(500, "设置失败", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "设置失败：" + e.getMessage(), null);
        }
    }

    /**
     * 管理员功能：获取销量最高的咖啡
     */
    @GetMapping("/admin/top-selling/{limit}")
    public Object getTopSellingCoffee(@PathVariable int limit) {
        try {
            List<Coffee> coffeeList = coffeeService.getTopSellingCoffee(limit);
            return new ResponseResult<>(200, "获取成功", coffeeList);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }
    
    /**
     * 首页功能：获取过去七天销量最高的咖啡
     */
    @GetMapping("/home/top-selling-last-week/{limit}")
    public Object getTopSellingLastWeek(@PathVariable int limit) {
        try {
            List<Map<String, Object>> coffeeList = coffeeService.getTopSellingLastWeek(limit);
            return new ResponseResult<>(200, "获取成功", coffeeList);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }

    /**
     * 管理员功能：获取按类别统计的销售数据
     */
    @GetMapping("/admin/sales-stats")
    public Object getCategorySalesStats() {
        try {
            List<Map<String, Object>> stats = coffeeService.getCategorySalesStats();
            return new ResponseResult<>(200, "获取成功", stats);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }

    /**
     * 管理员功能：获取所有不同的咖啡类别
     */
    @GetMapping("/admin/categories")
    public Object getAllCategories() {
        try {
            List<String> categories = coffeeService.getAllCategories();
            return new ResponseResult<>(200, "获取成功", categories);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }

    /**
     * 管理员功能：获取历史总销售额
     */
    @GetMapping("/admin/total-sales")
    public Object getTotalSalesAmount() {
        try {
            Double totalSales = coffeeService.getTotalSalesAmount();
            if (totalSales == null) {
                totalSales = 0.0;
            }
            return new ResponseResult<>(200, "获取成功", totalSales);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }
    
    /**
     * 管理员功能：获取历史总销量
     */
    @GetMapping("/admin/total-coffees-sold")
    public Object getTotalCoffeesSold() {
        try {
            Integer totalCoffeesSold = coffeeService.getTotalCoffeesSold();
            if (totalCoffeesSold == null) {
                totalCoffeesSold = 0;
            }
            return new ResponseResult<>(200, "获取成功", totalCoffeesSold);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }
    
    /**
     * 管理员功能：获取过去七天的每日销售额
     */
    @GetMapping("/admin/daily-sales-last-week")
    public Object getDailySalesForLastWeek() {
        try {
            List<Map<String, Object>> dailySales = orderService.getDailySalesForLastWeek();
            return new ResponseResult<>(200, "获取成功", dailySales);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }
}