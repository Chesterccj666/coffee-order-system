package com.lihao.CoffeeOrderSystem.controller;

import com.lihao.CoffeeOrderSystem.service.UserService;
import com.lihao.CoffeeOrderSystem.entity.User;
import com.lihao.CoffeeOrderSystem.utils.PinYinUtils;
import com.lihao.CoffeeOrderSystem.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Object login(@RequestBody User loginUser) {
        try {
            User user = userService.login(loginUser.getPhone(), loginUser.getPassword());
            if (user != null) {
                return new ResponseResult<>(200, "登录成功", user);
            } else {
                return new ResponseResult<>(401, "用户名或密码错误", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "登录失败：" + e.getMessage(), null);
        }
    }

    /**
     * 用户注册（支持头像上传）
     */
    @PostMapping("/register")
    public Object register(
            @RequestParam("username") String username,
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            @RequestParam("gender") String gender,
            @RequestParam("role") Integer role,
            @RequestParam(value = "headImage", required = false) MultipartFile headImage) {
        try {
            // 检查手机号是否已被注册
            User existingUser = userService.getUserByPhone(phone);
            if (existingUser != null) {
                return new ResponseResult<>(400, "该手机号已被注册", null);
            }

            // 创建用户对象
            User user = new User();
            user.setUsername(username);
            user.setPhone(phone);
            user.setPassword(password);
            user.setGender(gender);
            user.setRole(role);
            user.setRegisterTime(LocalDateTime.now()); // 设置注册时间为当前时间

            // 处理头像上传
            if (headImage != null && !headImage.isEmpty()) {
                // 将中文用户名转换为拼音（这里简化处理，实际应用中可以用专门的拼音库）
                String fileName = convertChineseToPinyin(username) + "-" + phone + ".jpg";
                
                // 使用系统临时目录或项目数据目录
                String uploadDir = System.getProperty("user.home") + "/CoffeeOrderSystem/upload/head_image/";
                
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                
                // 检查是否有同名文件存在，如果有则删除（处理同一用户重复注册的情况）
                File existingFile = new File(uploadDir + fileName);
                if (existingFile.exists()) {
                    boolean deleted = existingFile.delete();
                    if (!deleted) {
                        System.out.println("删除同名头像文件失败: " + uploadDir + fileName);
                    } else {
                        System.out.println("已删除同名头像文件: " + uploadDir + fileName);
                    }
                }
                
                // 保存文件
                File destFile = new File(uploadDir + fileName);
                headImage.transferTo(destFile);
                
                // 设置头像路径 - 使用相对路径以便通过Spring Boot静态资源配置访问
                user.setHeadImage("/upload/head_image/" + fileName);
            } else {
                // 使用默认头像
                user.setHeadImage("/static/head_image/default-avatar.jpg");
            }

            boolean success = userService.registerUser(user);
            if (success) {
                return new ResponseResult<>(200, "注册成功", null);
            } else {
                return new ResponseResult<>(500, "注册失败", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "注册失败：" + e.getMessage(), null);
        }
    }

    /**
     * 将中文转换为拼音（简化处理）
     */
    private String convertChineseToPinyin(String chinese) {
        return PinYinUtils.convertChineseToPinyin(chinese);
    }

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public Object getUserInfo(@PathVariable Integer id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                return new ResponseResult<>(200, "获取成功", user);
            } else {
                return new ResponseResult<>(404, "用户不存在", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败：" + e.getMessage(), null);
        }
    }

    /**
     * 更新用户信息（支持头像更新）
     */
    @PutMapping("/update")
    public Object updateUserInfo(
            @RequestParam("id") Integer id,
            @RequestParam("username") String username,
            @RequestParam("phone") String phone,
            @RequestParam("gender") String gender,
            @RequestParam("role") Integer role,
            @RequestParam(value = "headImage", required = false) MultipartFile headImage) {
        try {
            // 获取原用户信息
            User existingUser = userService.getUserById(id);
            if (existingUser == null) {
                return new ResponseResult<>(404, "用户不存在", null);
            }

            // 更新用户基本信息
            existingUser.setUsername(username);
            existingUser.setPhone(phone);
            existingUser.setGender(gender);
            existingUser.setRole(role);

            // 如果上传了新头像，则更新头像
            if (headImage != null && !headImage.isEmpty()) {
                // 删除旧头像文件（如果存在且不是默认头像）
                String oldImagePath = existingUser.getHeadImage();
                if (oldImagePath != null && !oldImagePath.contains("/static/head_image/default")) {
                    // 构建旧文件的完整路径
                    String uploadDir = System.getProperty("user.home") + "/CoffeeOrderSystem/upload/head_image/";
                    String fullOldImagePath = uploadDir + getFileNameFromPath(oldImagePath);
                    
                    File oldFile = new File(fullOldImagePath);
                    if (oldFile.exists()) {
                        boolean deleted = oldFile.delete();
                        if (!deleted) {
                            System.out.println("删除旧头像文件失败: " + fullOldImagePath);
                        } else {
                            System.out.println("已删除旧头像文件: " + fullOldImagePath);
                        }
                    }
                }

                // 生成新文件名
                String fileName = convertChineseToPinyin(username) + "-" + phone + ".jpg";
                
                // 使用系统临时目录或项目数据目录
                String uploadDir = System.getProperty("user.home") + "/CoffeeOrderSystem/upload/head_image/";
                
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                
                // 保存文件
                File destFile = new File(uploadDir + fileName);
                headImage.transferTo(destFile);
                
                // 设置头像路径
                existingUser.setHeadImage("/upload/head_image/" + fileName);
            }

            boolean success = userService.updateUser(existingUser);
            if (success) {
                // 返回更新后的用户信息（不含敏感信息如密码）
                User userInfo = new User();
                userInfo.setId(existingUser.getId());
                userInfo.setUsername(existingUser.getUsername());
                userInfo.setPhone(existingUser.getPhone());
                userInfo.setGender(existingUser.getGender());
                userInfo.setRole(existingUser.getRole());
                userInfo.setHeadImage(existingUser.getHeadImage());
                userInfo.setRegisterTime(existingUser.getRegisterTime());
                
                return new ResponseResult<>(200, "更新成功", userInfo);
            } else {
                return new ResponseResult<>(500, "更新失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace(); // 添加日志记录
            return new ResponseResult<>(500, "更新失败：" + e.getMessage(), null);
        }
    }
    
    /**
     * 修改用户密码
     */
    @PutMapping("/changePassword")
    public Object changePassword(@RequestBody Map<String, String> requestData) {
        try {
            Integer userId = Integer.parseInt(requestData.get("userId"));
            String oldPassword = requestData.get("oldPassword");
            String newPassword = requestData.get("newPassword");
            
            boolean success = userService.changePassword(userId, oldPassword, newPassword);
            if (success) {
                return new ResponseResult<>(200, "密码修改成功", null);
            } else {
                return new ResponseResult<>(400, "原密码错误或修改失败", null);
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "密码修改失败：" + e.getMessage(), null);
        }
    }
    
    /**
     * 从路径中提取文件名
     */
    private String getFileNameFromPath(String path) {
        if (path == null) {
            return null;
        }
        int lastSlashIndex = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        if (lastSlashIndex >= 0) {
            return path.substring(lastSlashIndex + 1);
        }
        return path;
    }
}