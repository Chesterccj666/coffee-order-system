package com.lihao.CoffeeOrderSystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        String uploadPath = System.getProperty("user.home") + "/CoffeeOrderSystem/upload/";
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath);
        
        // 配置静态资源路径
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}